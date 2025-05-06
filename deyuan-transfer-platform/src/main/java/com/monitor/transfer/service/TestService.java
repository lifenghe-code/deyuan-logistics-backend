package com.monitor.transfer.service;

import com.monitor.transfer.middleware.HybridRouter;
import com.monitor.transfer.middleware.KafkaToNetty;
import com.monitor.transfer.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

@Component
@Slf4j
public class TestService {

    @Value("${kafka.topic}")
    private String topic;

    @Resource
    HybridRouter hybridRouter;

    @Resource
    KafkaToNetty kafkaToNetty;




    @Scheduled(fixedRate = 100)
    public void runContinuously() throws IOException {
        log.info("定时异步持续任务执行: " + System.currentTimeMillis());
        byte[] bytes = ImageUtil.imageToByteArray("assets/img/spiderman.jpg");
        String tmp = "123";

        hybridRouter.sendData(bytes);

    }
}
