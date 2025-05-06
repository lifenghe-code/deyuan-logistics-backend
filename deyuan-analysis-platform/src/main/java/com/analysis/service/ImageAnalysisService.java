package com.analysis.service;

import com.analysis.manager.ComputeThreadPool;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ImageAnalysisService {
    private static  AtomicInteger count = new AtomicInteger(0);
    public static void process(Object object) throws InterruptedException {
        ComputeThreadPool.submit(()->{
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("当前线程：" + Thread.currentThread().getName() + "，第" + count + "次处理完成");
            count.incrementAndGet();
            // 发现违规情况后，调用接口进行行为记录
            // String url = "127.0.0.1:8101/api/detect/violation";
            // Map<String, Object> body= new HashMap<>();
            // HttpUtil.post(url,body);
        });
    }
}
