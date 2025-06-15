package com.deyuan.client;

import com.deyuan.client.client.NettyClient;
import com.deyuan.client.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        NettyClient client = new NettyClient();
        new Thread(() -> {
            client.connect("127.0.0.1", 8000);
        }).start();
        // 等待连接成功
        Thread.sleep(5000);
        for (int i = 0; i < 1000; i++) {
            byte[] bytes = ImageUtil.imageToByteArray("assets/img/image1.png");

            // byte[] bytes = "213".getBytes();
            client.send(bytes);
            Thread.sleep(5000);
        }
    }
}
