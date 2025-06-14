package com.monitor.transfer.middleware;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.monitor.transfer.constant.MapConstant;
import com.monitor.transfer.protocol.CustomProtocol;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
        props.put("max.poll.records", "100");  // 默认是500
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public void start() {
        new Thread(() -> {
            while (running) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, byte[]> record : records) {



                    CustomProtocol myObj = ObjectUtil.deserialize(record.value());

                    MapConstant.analysisChannels.writeAndFlush(myObj);
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
