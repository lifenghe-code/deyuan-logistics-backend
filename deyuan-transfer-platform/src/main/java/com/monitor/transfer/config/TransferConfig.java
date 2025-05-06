package com.monitor.transfer.config;

import com.monitor.transfer.middleware.HybridRouter;
import com.monitor.transfer.middleware.KafkaToNetty;
import com.monitor.transfer.middleware.NettySender;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;
import java.util.Properties;


@Configuration
public class TransferConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topic}")
    private String topic;

    @Value("${netty.host}")
    private String nettyHost;

    @Value("${netty.port}")
    private int nettyPort;

    @Value("${token-bucket.capacity}")
    private int capacity;

    @Value("${token-bucket.highThreshold}")
    private int highThreshold;

    @Value("${token-bucket.lowThreshold}")
    private int lowThreshold;

    @Value("${token-bucket.coolDownPeriodMs}")
    private long coolDownPeriodMs;

    @Resource
    private  KafkaMessageProducer kafkaMessageProducer; // 自动注入已存在的Bean

    @Bean
    public NettySender nettySender() throws InterruptedException {
        NettySender nettySender = new NettySender(nettyHost, nettyPort);
        nettySender.start();
        return nettySender;

    }

    @Bean
    public HybridRouter hybridRouter(NettySender nettySender) {
        return new HybridRouter
                (nettySender, kafkaMessageProducer, topic, capacity,highThreshold,lowThreshold,coolDownPeriodMs);
    }
    @Bean
    public KafkaToNetty kafkaToNetty(NettySender nettySender){
        return new KafkaToNetty(nettySender,bootstrapServers,topic);
    }

//    @Bean
//    public KafkaToNettyBridge kafkaBridge(NettySender nettySender) {
//        KafkaToNettyBridge bridge = new KafkaToNettyBridge(nettySender);
//        bridge.start();
//        return bridge;
//    }
}
