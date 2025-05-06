package com.monitor.transfer.client;

import com.monitor.transfer.handler.HeartbeatClientHandler;
import com.monitor.transfer.handler.HeartbeatHandler;
import com.monitor.transfer.handler.NettyClientHandler;
import com.monitor.transfer.handler.ReconnectHandler;
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
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class NettyClient {
    public void connect(String hostname,int port) {
        // 处理TCP连接请求
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 用于引导和绑定服务器
            Bootstrap bootstrap = new Bootstrap();

            //将上面的线程组加入到 bootstrap 中
            bootstrap.group(group)
                    //将通道设置为异步的通道
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline()
                                    .addLast(new ReconnectHandler(NettyClient.this, 5, 10))
                                    .addLast(new CustomDecoder())         // 解码器
                                    .addLast(new CustomEncoder())         // 自定义协议编码器
                                    .addLast(new HeartbeatClientHandler())         // 自定义心跳处理器
                                    .addLast(new NettyClientHandler());
                        }
                    })

                    .option(ChannelOption.TCP_NODELAY,true)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024*1024*2));


            // 连接到远程节点，阻塞等待直到连接完成。
            ChannelFuture future = bootstrap.connect(hostname, port).sync();

            if (future.isSuccess()) {
                Channel channel = future.channel();
                System.out.println("成功连接到 " + channel.remoteAddress());
                // ByteBuf buf = Unpooled.copiedBuffer("123", CharsetUtil.UTF_8);
                // channel.writeAndFlush(buf);
                // startConsoleThread(channel);
            }

            // 阻塞，直到 channel 关闭。
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭线程池并释放所有资源。
            group.shutdownGracefully();
        }
    }

    private void startConsoleThread(Channel channel) {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (!Thread.interrupted()) {
                if (channel.isActive()) {
                    System.out.print("请输入消息: ");
                    String input = scanner.nextLine();
                    CustomProtocol message = new CustomProtocol();
                    message.setType(MessageType.DATA);
                    message.setContent(input.getBytes(StandardCharsets.UTF_8));
                    message.setLength(input.getBytes().length);
                    channel.writeAndFlush(message);
                }
            }
        }).start();
    }
}
