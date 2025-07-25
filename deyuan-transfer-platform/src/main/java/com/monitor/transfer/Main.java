package com.monitor.transfer;


import com.monitor.transfer.config.KafkaMessageProducer;
import com.monitor.transfer.middleware.HybridRouter;
import com.monitor.transfer.middleware.KafkaToNetty;
import com.monitor.transfer.server.NettyServer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Main {


    private static String bootstrapServers = "127.0.0.1:9092";


    private static  String topic = "netty-traffic";



    private static int capacity = 10;


    private static int highThreshold = 7;


    private static int lowThreshold = 3;


    private static long coolDownPeriodMs = 5000;

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        // props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // 启用幂等性

        KafkaProducer<String, byte[]> kafkaProducer = new KafkaProducer<>(props);

        KafkaMessageProducer kafkaMessageProducer = new KafkaMessageProducer(kafkaProducer);
        HybridRouter hybridRouter = new HybridRouter(kafkaMessageProducer,topic,capacity,highThreshold,lowThreshold,coolDownPeriodMs);
        KafkaToNetty kafkaToNetty = new KafkaToNetty(bootstrapServers,topic);
        kafkaToNetty.start();
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(8000);
        // 本地加载数据，模拟接口获取数据
    }
}
