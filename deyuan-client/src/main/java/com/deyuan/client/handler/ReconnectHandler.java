package com.deyuan.client.handler;

import com.deyuan.client.client.NettyClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ReconnectHandler extends ChannelInboundHandlerAdapter {
    private NettyClient client;
    private int retryCount = 0;
    private int maxRetries = 5;
    private final long delay;

    public ReconnectHandler(NettyClient client, int maxRetries, long delay) {
        this.client = client;
        this.maxRetries = maxRetries;
        this.delay = delay;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("连接成功，重置重试计数器");
        retryCount = 0; // 连接成功时重置计数器
        ctx.fireChannelActive();
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws InterruptedException {
        if (retryCount < maxRetries) {
            retryCount++;

            log.warn("连接断开，尝试重连({}/{}), {}秒后重试", retryCount, maxRetries, delay);



                log.info("开始第 {} 次重连尝试...", retryCount);
                try {

                    client.connect("127.0.0.1", 8000);

                } catch (Exception e) {
                    log.error("连接过程中异常: {}", e.toString());
                    Thread.sleep(delay * 1000);
                    channelInactive(ctx); // 继续重连
                }

        } else {
            log.error("达到最大重试次数({})，停止重连", maxRetries);
        }
    }
}
