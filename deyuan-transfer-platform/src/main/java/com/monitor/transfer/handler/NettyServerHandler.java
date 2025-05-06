package com.monitor.transfer.handler;

import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;
import com.monitor.transfer.utils.ImageUtil;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable // 标识一个 channelHandler 可以被多个 channel 安全地调用。
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    // 维护活跃连接
    private static final ChannelGroup activeChannels =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final AtomicInteger currentIndex = new AtomicInteger(0);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        // byte[] bytes = ImageUtil.imageToByteArray("assets/img/img1.png");
        //
        log.info("Channel激活: " + ctx.channel().id());
        String data = "Channel激活: " + ctx.channel().id();
        activeChannels.add(ctx.channel());
        CustomProtocol message = new CustomProtocol();
        message.setType(MessageType.DATA);
        message.setContent(data.getBytes());
        message.setLength(data.getBytes().length);
        ctx.writeAndFlush(message);
    }
//    @Override
//    public void channelRegistered(ChannelHandlerContext ctx) {
//        log.info("Channel注册: " + ctx.channel().id());
//        String data = "Channel注册: " + ctx.channel().id();
//        CustomProtocol message = new CustomProtocol();
//        byte[] bytes = StrUtil.bytes(data);
//        message.setType(MessageType.DATA);
//        message.setContent(bytes);
//        message.setLength(bytes.length);
//        ctx.writeAndFlush(message);
//        ctx.fireChannelRegistered();
//    }
    @Override
    // 当有入站消息时该方法就会调用
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CustomProtocol message = (CustomProtocol)msg;
        MessageType type = message.getType();

        if(type==MessageType.DATA) {
            String content = new String(message.getContent(), StandardCharsets.UTF_8);
            log.info("服务器收到消息，数据长度" + message.getLength());
            // ImageUtil.saveByteArrayAsImage(message.getContent(),"a.png");
            // 将接收到的消息写给发送者，而不冲刷出站消息。
            // ctx.writeAndFlush(buffer);
            activeChannels.stream()
                    .filter(ch -> ch != ctx.channel()) // 关键过滤条件
                    .forEach(ch -> ch.writeAndFlush(message));
        }
        else {
            // 其他类型消息传递给下一个handler
            ctx.fireChannelRead(message);
        }

    }

//    @Override
//    // channelRead消费完读取的数据的时候被触发
//    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        // 将未决消息冲刷到远程节点，并且关闭该 channel
//        ChannelFuture channelFuture = ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
//        channelFuture.addListener(ChannelFutureListener.CLOSE);
//    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        activeChannels.remove(ctx.channel());
        log.info("客户端断开: {}", ctx.channel().remoteAddress());
    }
    @Override
    // 在读操作时处理异常
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 打印异常栈，并关闭该 channel
        cause.printStackTrace();
        ctx.close();
    }
    //读写超时并且添加IdleStateHandler时，会触发这个方法
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        //获取超时对象，读超时，写超时还是读写超时
//        IdleState state = ((IdleStateEvent) evt).state();
//        //如果是读写超时，我这里整的简单了
//        if (state.equals(IdleState.ALL_IDLE)) {
//            log.info("客户端发送心跳");
//            //给服务端端发送字符为（HeartBeat-req）的心跳请求
//            ctx.writeAndFlush(Unpooled.copiedBuffer("HeartBeat-req".getBytes()));
//        }

//    }
    /**
     * 向指定客户端发送消息
     * @param channelId 客户端通道ID
     * @param message 要发送的消息
     * @return 发送是否成功
     */
    public void sendMessage(String channelId, Object message) {
        // 遍历所有活跃的 Channel
        for (Channel channel : activeChannels) {
            if (Objects.equals(channel.id().asShortText(), channelId)){
                if (channel.isActive()) {
                    channel.writeAndFlush(message).addListener(f -> {
                        if (!f.isSuccess()) {
                            log.info("消息发送失败: " + f.cause().getMessage());
                        }
                    });
                    return;
                }
            }
        }
        log.info("未找到匹配的活跃Channel: " + channelId);
    }

    // 轮询发送消息
    public void sendMessage(Object message) {
        if (activeChannels.isEmpty()) {
            log.info("没有活跃的客户端连接");
            return;
        }

        // 获取当前轮询索引
        int index = currentIndex.getAndUpdate(i -> (i + 1) % activeChannels.size());

        // 转换为数组避免并发修改问题
        Channel[] channels = activeChannels.toArray(new Channel[0]);
        Channel targetChannel = channels[index];

        if (targetChannel.isActive()) {
            ChannelFuture future = targetChannel.writeAndFlush(message);
            future.addListener(f -> {
                if (f.isSuccess()) {
                    System.out.println("消息成功发送到: " + targetChannel.id().asShortText());
                } else {
                    System.err.println("消息发送失败到 " + targetChannel.id().asShortText() +
                            ": " + f.cause().getMessage());
                }
            });
        } else {
            // 如果当前Channel不活跃，递归调用尝试下一个
            sendMessage(message);
        }
    }
    /**
     * 广播消息给所有客户端
     * @param message 要广播的消息
     */
    public void broadcast(Object message) {
        activeChannels.forEach(channel -> {
            if (channel.isActive()) {
                channel.writeAndFlush(message).addListener(f -> {
                    if (!f.isSuccess()) {
                        log.error("广播消息失败到 {}: {}",
                                channel.id().asShortText(), f.cause().getMessage());
                    }
                });
            }
        });
    }
}
