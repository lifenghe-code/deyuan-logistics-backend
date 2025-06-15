package com.deyuan.client.client;

import com.deyuan.client.constant.ClientConstant;
import com.deyuan.client.handler.HeartbeatClientHandler;
import com.deyuan.client.handler.NettyClientHandler;
import com.deyuan.client.handler.ReconnectHandler;

import com.deyuan.client.protocol.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {

    private String host;
    private Integer port;
    private Channel channel;
    private EventLoopGroup eventLoopGroup;


    public void connect(String hostname,int port) {
        // 处理TCP连接请求
        this.eventLoopGroup = new NioEventLoopGroup();
        try {
            // 用于引导和绑定服务器
            Bootstrap bootstrap = new Bootstrap();

            //将上面的线程组加入到 bootstrap 中
            bootstrap.group(this.eventLoopGroup)
                    //将通道设置为异步的通道
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.attr(ClientType.CLIENT_TYPE_KEY).set(ClientType.VEHICLE); // 示例值
                            socketChannel.attr(ClientType.CLIENT_ID).set(ClientConstant.CLIENT_ID); // 示例ID
                            ChannelPipeline pipeline = socketChannel.pipeline()
                                    // .addLast(new AuthHandler())
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
                this.channel = future.channel();

                log.info("成功连接到 {}", channel.remoteAddress());
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
            this.eventLoopGroup.shutdownGracefully();
        }
    }

    public void send(byte[] data) {
        if (channel.isActive()) {
            CustomProtocol message = new CustomProtocol();
            message.setMessageType(MessageType.DATA);
            message.setClientType(ClientType.VEHICLE);
            message.setClientId(ClientConstant.CLIENT_ID);
            message.setContent(data);
            message.setLength(data.length);
            channel.writeAndFlush(message).addListener(future -> {
                if (!future.isSuccess()) {
                    // 失败处理
                    log.error("Netty发送失败: " + future.cause());
                }
                else{
                    log.info("Netty发送成功: ");
                }
            });
        }
    }
    public void shutdown() {
        channel.close().syncUninterruptibly();
        eventLoopGroup.shutdownGracefully();
    }
}
