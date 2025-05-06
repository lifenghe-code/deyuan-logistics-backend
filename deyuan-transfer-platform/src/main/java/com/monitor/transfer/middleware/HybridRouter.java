package com.monitor.transfer.middleware;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.monitor.transfer.config.KafkaMessageProducer;
import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;
import org.springframework.stereotype.Component;

@Slf4j
public class HybridRouter {
    private  TokenBucket tokenBucket;
    private  NettySender nettySender;
    private  KafkaMessageProducer kafkaMessageProducer;
    private final String kafkaTopic;

    public HybridRouter(NettySender nettySender,
                        KafkaMessageProducer kafkaMessageProducer,
                        String kafkaTopic,
                        int capacity,
                        int initialHighThreshold,
                        int initialLowThreshold,
                        long coolDownMs) {
        this.nettySender = nettySender;
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
        scheduler.scheduleAtFixedRate(() -> tokenBucket.refill(10),
                0, 1000, TimeUnit.MILLISECONDS); // 每100ms补充10个令牌
    }

    public void sendData(byte[] data) {
        if (tokenBucket.shouldUseKafka()) {
            // Kafka模式
            log.info("发送到Kafka");
            sendToKafka(data);
        } else {
            // Netty模式
            if (tokenBucket.tryAcquire()) {

                nettySender.sendDirect(data);
            } else {
                log.info("发送到Kafka");
                sendToKafka(data);
            }
        }
    }

    public void sendToKafka(byte[] data) {

        kafkaMessageProducer.sendAsync(data, (metadata, exception) -> {
            if (exception != null) {
                // Kafka 不可用时会触发这里
                log.error("Kafka 异步发送失败，已降级到本地磁盘：" + exception.getMessage());
            } else {
                log.info("Kafka 异步发送成功！");
            }
        });
    }



    // 获取当前可用请求数(用于监控)

}