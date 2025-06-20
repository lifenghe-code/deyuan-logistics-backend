package com.monitor.transfer.middleware;

import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.utils.CustomProtocolSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
public class KafkaToNetty {

    private String bootstrapServers;

    private String topic;
    private final KafkaConsumer<String, byte[]> consumer;


    private volatile boolean running = true;

    public KafkaToNetty(String bootstrapServers, String topic) {

        //创建Properties对象，配置 Kafka 生产者的各种参数
        Properties props = new Properties() ;
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "1");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", ByteArrayDeserializer.class.getName());
        // 设置每次poll最多拉取100条消息
        props.put("max.poll.records", "10");  // 默认是500
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public void start() {
        new Thread(() -> {
            while (running) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, byte[]> record : records) {
                    log.info("拉取Kafka中的消息，通过Netty发送");
                    try {
                        CustomProtocol deserialize = CustomProtocolSerializer.deserialize(record.value());
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

//                    MapConstant.analysisChannels.writeAndFlush(myObj);
                    log.info("拉取Kafka中的消息，通过Netty发送");
                }
            }
        }).start();
    }

    public void stop() {
        running = false;
        consumer.close();
    }
}
