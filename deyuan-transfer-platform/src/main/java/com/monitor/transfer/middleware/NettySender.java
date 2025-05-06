package com.monitor.transfer.middleware;

import com.monitor.transfer.handler.NettyClientHandler;
import com.monitor.transfer.protocol.CustomDecoder;
import com.monitor.transfer.protocol.CustomEncoder;
import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettySender {
    private  AtomicInteger count = new AtomicInteger(0);
    private String host;
    private Integer port;
    private  Channel channel;
    private  EventLoopGroup eventLoopGroup;


    public NettySender(String nettyHost, int nettyPort) {
        this.host = nettyHost;
        this.port = nettyPort;
    }

    public void start() {
        new Thread(() -> {
        this.eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline()
                                .addLast(new CustomEncoder())         // 自定义协议编码器
                                .addLast(new CustomDecoder())         // 解码器
                                .addLast(new NettyClientHandler());
                    }
                })
                // .option(ChannelOption.SO_KEEPALIVE, true) // 启用TCP KeepAlive
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024*1024*2));
            ChannelFuture future = null;
            try {
                future = bootstrap.connect(host, port).sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (future.isSuccess()) {
            this.channel = future.channel();
            System.out.println("成功连接到 " + channel.remoteAddress());
            // ByteBuf buf = Unpooled.copiedBuffer("123", CharsetUtil.UTF_8);
            // channel.writeAndFlush(buf);
            // startConsoleThread(channel);
        }
        // 阻塞，直到 channel 关闭。
            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void sendDirect(byte[] data) {
        if (channel.isActive()) {
            CustomProtocol message = new CustomProtocol();
            message.setType(MessageType.DATA);
            message.setContent(data);
            message.setLength(data.length);
            channel.writeAndFlush(message).addListener(future -> {
                if (!future.isSuccess()) {
                    // 失败处理
                    log.error("Netty发送失败: " + future.cause());
                }
                else{
                    log.info("当前线程：" + Thread.currentThread().getName() + "，第" + count + "次发送完成");

                    log.info("Netty发送成功: ");
                    count.incrementAndGet();
                }
            });
        }
    }

    public void shutdown() {
        channel.close().syncUninterruptibly();
        eventLoopGroup.shutdownGracefully();
    }
}
