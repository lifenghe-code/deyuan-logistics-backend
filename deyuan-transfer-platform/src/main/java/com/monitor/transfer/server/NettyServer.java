
package com.monitor.transfer.server;
import com.monitor.transfer.handler.HeartbeatServerHandler;
import com.monitor.transfer.handler.NettyServerHandler;
import com.monitor.transfer.handler.NetworkEvaluateHandler;
import com.monitor.transfer.protocol.CustomDecoder;
import com.monitor.transfer.protocol.CustomEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;


public class NettyServer {
    private final NettyServerHandler serverHandler = new NettyServerHandler();
    public void start(int port){
        // 处理TCP连接请求
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 处理I/O事件
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // 用于引导和绑定服务器
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 在Pipeline中必须添加StringEncoder才能发送字符串
            //将上面的线程组加入到 bootstrap 中
            bootstrap.group(bossGroup,workGroup)
                    //将通道设置为异步的通道
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 因为 NettyServerHandler 被标注为 @Sharable，所以可以使用相同的实例
                        socketChannel.pipeline()
                        .addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS))
                                .addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                                        // lengthFieldLength 表示用4个字节表示数据长度
                                Integer.MAX_VALUE, 18, 4, 0, 0)) // 拆包（不去除 header，保留type）
                                .addLast("customDecoder",new CustomDecoder())         // 解码器
                                .addLast(new CustomEncoder())         // 自定义协议编码器
                                .addLast(new HeartbeatServerHandler())         // 自定义心跳处理器
                                .addLast(new NetworkEvaluateHandler()) //使用共享的handler实例
                                .addLast(serverHandler); //使用共享的handler实例
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,200)
                    // .option(ChannelOption.SO_RCVBUF,1024*1024*2)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024*1024*2))
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            // 异步绑定服务器，调用 sync() 方法阻塞等待直到绑定完成。
            ChannelFuture future = bootstrap.bind(port).sync();
            // 获取 channel 的 closeFuture，并且阻塞直到它完成。
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
    /**
     * 向指定客户端发送消息
     * @param channelId 客户端通道ID
     * @param message 要发送的消息
     */
    public void sendToClient(String channelId, Object message) {
        serverHandler.sendMessage(channelId, message);
    }

    /**
     * 轮询方式发送消息给客户端
     * @param message 要发送的消息
     */
    public void sendRoundRobin(Object message) {
        serverHandler.sendMessage(message);
    }

    /**
     * 广播消息给所有客户端
     * @param message 要广播的消息
     */
    public void broadcast(Object message) {
        serverHandler.broadcast(message);
    }
}

