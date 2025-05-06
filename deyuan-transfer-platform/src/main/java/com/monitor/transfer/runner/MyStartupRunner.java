package com.monitor.transfer.runner;
import com.monitor.transfer.middleware.HybridRouter;
import com.monitor.transfer.middleware.KafkaToNetty;
import org.springframework.beans.factory.annotation.Value;
import com.monitor.transfer.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
@Slf4j
public class MyStartupRunner implements CommandLineRunner {
//    @Value("${kafka.topic}")
//    private String topic;
//
//    @Resource
//    HybridRouter hybridRouter;
//
//    @Resource
//    KafkaToNetty kafkaToNetty;

    @Override
    public void run(String... args) throws Exception {
        log.info("异步持续任务执行: " + System.currentTimeMillis());
    }
}
