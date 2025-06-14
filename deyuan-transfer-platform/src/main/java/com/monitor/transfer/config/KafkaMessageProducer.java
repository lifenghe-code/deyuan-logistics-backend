package com.monitor.transfer.config;

import com.monitor.transfer.protocol.CustomProtocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description kafka消息发送
 * @Date 2025/4/3 10:47 上午
 * @Author li-fh
 */

@Slf4j
public class KafkaMessageProducer {

    private final KafkaProducer<String, byte[]> kafkaProducer;


    private static String fallbackDirectory =  "./kafka_fallback";

    private String topic = "netty-traffic";

    private static BufferedWriter fallbackWriter;
    private static Path fallbackFilePath;



    static {
        // 创建回退目录
        Path dirPath = Paths.get(fallbackDirectory);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 创建回退文件
        fallbackFilePath = dirPath.resolve("messages_" + System.currentTimeMillis() + ".log");
        try {
            fallbackWriter = Files.newBufferedWriter(fallbackFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Initialized Kafka fallback mechanism. Messages will be stored at: {}", fallbackFilePath);
    }

    public KafkaMessageProducer(KafkaProducer<String, byte[]> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PreDestroy
    public void cleanup() throws IOException {
        if (kafkaProducer != null) {
            kafkaProducer.flush(); // 确保所有回调执行
            kafkaProducer.close();
        }
        if (fallbackWriter != null) {
            fallbackWriter.close();
        }
    }
    /**
     * 同步发送消息
     */
    public void send(byte[] data) {
        String key = UUID.randomUUID().toString();
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, data);
        try {
            kafkaProducer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Kafka发送失败，写入本地磁盘: " + exception.getMessage());
                    writeToLocalDisk(topic, key, data);
                }
            }).get(5, TimeUnit.SECONDS); // 同步等待发送结果
        } catch (Exception e) {
            log.error("Kafka同步发送失败，直接写入磁盘: " + e.getMessage());
            writeToLocalDisk(topic, key, data);
        }
    }

    /**
     * 异步发送消息
     */
    public void sendAsync( byte[] data, Callback callback) {
        String key = UUID.randomUUID().toString();
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, data);


            kafkaProducer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Kafka async send failed for key [{}], switching to fallback", key, exception);

                    writeToLocalDisk(topic,key, data);

                    // 告知外部 callback：Kafka 失败，已写入本地磁盘
                    if (callback != null) {
                        callback.onCompletion(null, exception);
                    }
                } else {
                    // Kafka 成功发送
                    if (callback != null) {
                        callback.onCompletion(metadata, null);
                    }
                }
            });

            // 即便未发送 Kafka，也通知 callback 表示已持久化
            if (callback != null) {
                callback.onCompletion(null, null);
            }
    }

    public void sendAsync(CustomProtocol message, Callback callback) {
        String key = UUID.randomUUID().toString();
        byte[] data = message.getContent();
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, data);


        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Kafka async send failed for key [{}], switching to fallback", key, exception);

                writeToLocalDisk(topic,key, data);

                // 告知外部 callback：Kafka 失败，已写入本地磁盘
                if (callback != null) {
                    callback.onCompletion(null, exception);
                }
            } else {
                // Kafka 成功发送
                if (callback != null) {
                    callback.onCompletion(metadata, null);
                }
            }
        });

        // 即便未发送 Kafka，也通知 callback 表示已持久化
        if (callback != null) {
            callback.onCompletion(null, null);
        }
    }
    private synchronized void writeToLocalDisk(String topic,String key, byte[] data) {
        try {
            // 这里简单使用 Base64 编码字节数组，方便记录为文本
            String encoded = java.util.Base64.getEncoder().encodeToString(data);
            fallbackWriter.write(topic + '|' + key + "|" + encoded);
            fallbackWriter.newLine();
        } catch (IOException e) {
            log.error("写入本地备份失败: " + e.getMessage());
        }
    }

    private synchronized void processBacklog() {
        try {
            // 关闭当前writer以处理文件
            fallbackWriter.close();

            if (Files.size(fallbackFilePath) > 0) {
                log.info("Processing backlog messages from {}", fallbackFilePath);

                try (BufferedReader reader = Files.newBufferedReader(fallbackFilePath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\\|", 3);
                        if (parts.length == 3) {
                            String topic = parts[0];
                            String key = parts[1].isEmpty() ? null : parts[1];
                            byte[] value = parts[2].getBytes();

                            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, value);
                            try {
                                kafkaProducer.send(record).get(200, TimeUnit.MILLISECONDS);
                                log.debug("Successfully resent backlog message to topic {}", topic);
                            } catch (Exception e) {
                                // 如果重发失败，停止处理并重新打开fallback文件
                                log.error("Failed to resend backlog message, aborting", e);
                                fallbackWriter = Files.newBufferedWriter(fallbackFilePath, StandardOpenOption.APPEND);
                                return;
                            }
                        }
                    }
                }

                // 处理完所有积压消息后，删除文件并创建新的
                Files.delete(fallbackFilePath);
                fallbackFilePath = Paths.get(fallbackDirectory, "messages_" + System.currentTimeMillis() + ".log");
            }

            // 重新打开writer
            fallbackWriter = Files.newBufferedWriter(fallbackFilePath);
        } catch (IOException e) {
            log.error("Error processing backlog messages", e);
            try {
                // 尝试恢复writer
                if (fallbackWriter == null) {
                    fallbackWriter = Files.newBufferedWriter(fallbackFilePath, StandardOpenOption.APPEND);
                }
            } catch (IOException ex) {
                log.error("Failed to recover fallback writer", ex);
            }
        }
    }
}