package com.monitor.transfer.middleware;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.monitor.transfer.config.DiskBackupQueue;
import com.monitor.transfer.config.KafkaMessageProducer;
import com.monitor.transfer.constant.MapConstant;
import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.utils.CustomProtocolSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;

@Slf4j
public class HybridRouter {
    private static final Object LOCK = new Object();


    private static TokenBucket tokenBucket;
    private static KafkaMessageProducer kafkaMessageProducer;
    private final String kafkaTopic;

    public HybridRouter(
                        KafkaMessageProducer kafkaMessageProducer,
                        String kafkaTopic,
                        int capacity,
                        int initialHighThreshold,
                        int initialLowThreshold,
                        long coolDownMs) {

        this.kafkaMessageProducer = kafkaMessageProducer;
        this.kafkaTopic = kafkaTopic;
        this.tokenBucket = new TokenBucket(
                capacity,
                initialHighThreshold,
                initialLowThreshold,
                coolDownMs
        );

        // 启动令牌补充线程
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> tokenBucket.refill(100),
                0, 1000, TimeUnit.MILLISECONDS); // 每1000ms补充100个令牌
    }

    public static void sendData(byte[] data) {
        // nettySender.sendDirect(data);


        if (tokenBucket.shouldUseKafka()) {
            // Kafka模式
            log.info("发送到Kafka");
            sendToKafka(data);
        } else {
            // Netty模式
            if (tokenBucket.tryAcquire()) {
                log.info(MapConstant.analysisMap.get("200000"));
                log.info(String.valueOf(MapConstant.analysisMap.size()));
                MapConstant.analysisChannels.writeAndFlush(data);
                log.info("直接通过Netty发送");
            } else {
                log.info("发送到Kafka");
                sendToKafka(data);
            }
        }
    }
    public static void sendData(CustomProtocol data) throws IOException {

        log.info("直接通过Netty发送");

        if (tokenBucket.shouldUseKafka()) {
            // Kafka模式
            log.info("发送到Kafka");
            byte[] serialize = CustomProtocolSerializer.serialize(data);

            sendToKafka(serialize);
        } else {
            // Netty模式
            if (tokenBucket.tryAcquire()) {
                MapConstant.analysisChannels.writeAndFlush(data);
                log.info("直接通过Netty发送");
            } else {
                log.info("发送到Kafka");
                sendToKafka(data.getContent());
            }
        }
    }

    public static void sendToKafka(byte[] data) {

        kafkaMessageProducer.sendAsync(data, (metadata, exception) -> {
            if (exception != null) {
                // Kafka 不可用时会触发这里
                DiskBackupQueue tmp = new DiskBackupQueue(LocalDateTime.now().toString());
                ProducerRecord<byte[], byte[]> record = new ProducerRecord<>("netty-traffic", LocalDateTime.now().toString().getBytes(), data);
                try {
                    tmp.add(record);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                log.error("异步发送至 Kafka 失败，已降级到本地磁盘：" + exception.getMessage());
            } else {
                log.info("异步发送到 Kafka 成功！");
            }
        });
    }


    public void sendToKafka(CustomProtocol data) {
        kafkaMessageProducer.sendAsync(data, (metadata, exception) -> {
            if (exception != null) {
                // Kafka 不可用时会触发这里
                // 这里加锁，防止多辆车的数据同一时间进行落盘
                synchronized(LOCK){
                    DiskBackupQueue diskBackupQueue = new DiskBackupQueue(LocalDateTime.now().toString());
                    log.error("Kafka 异步发送失败，已降级到本地磁盘：" + exception.getMessage());
                }
            } else {
                log.info("Kafka 异步发送成功！");
            }
        });
    }



    // 获取当前可用请求数(用于监控)

}