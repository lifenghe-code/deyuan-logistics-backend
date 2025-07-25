package com.monitor.transfer.handler;

import com.monitor.transfer.constant.MapConstant;
import com.monitor.transfer.constant.MessageConstant;
import com.monitor.transfer.middleware.HybridRouter;
import com.monitor.transfer.protocol.ClientType;
import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;

import io.netty.channel.*;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import java.util.concurrent.atomic.AtomicInteger;


@ChannelHandler.Sharable // 标识一个 channelHandler 可以被多个 channel 安全地调用。
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private HybridRouter hybridRouter;
    // 维护活跃连接
    private static final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException {
        // byte[] bytes = ImageUtil.imageToByteArray("assets/img/img1.png");
        //
        log.info("Channel激活，Channel类型为: " + ctx.channel().id());
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
        MessageType type = message.getMessageType();
        if(type==MessageType.DATA) {
            String content = new String(message.getContent(), StandardCharsets.UTF_8);
            log.info("服务器收到消息，数据长度" + message.getLength());
            // ImageUtil.saveByteArrayAsImage(message.getContent(),"a.png");
            // 将接收到的消息写给发送者，而不冲刷出站消息。
            // ctx.writeAndFlush(buffer);
            ctx.channel().writeAndFlush(MessageConstant.REPLY_MESSAGE);

//            MapConstant.analysisChannels.stream()
//                    .filter(ch -> ch != ctx.channel()) // 关键过滤条件
//                    .forEach(ch -> ch.writeAndFlush(message));

            HybridRouter.sendData(message);
        }
        else if(type==MessageType.AUTH) {
            String content = new String(message.getContent(), StandardCharsets.UTF_8);

            // ImageUtil.saveByteArrayAsImage(message.getContent(),"a.png");
            // 将接收到的消息写给发送者，而不冲刷出站消息。
            // ctx.writeAndFlush(buffer);
            if(message.getClientType().getValue() == ClientType.VEHICLE.getValue()) {
                log.info("车载端ChannelId为" + ctx.channel().id().asShortText());
                MapConstant.vehicleChannels.add(ctx.channel());

                MapConstant.vehicleMap.putIfAbsent(message.getClientId(), String.valueOf(ctx.channel().id().asShortText()));
            }
            else if(message.getClientType().getValue() == ClientType.ANALYSIS.getValue()) {
                log.info("数据分析端ChannelId为" + ctx.channel().id());
                MapConstant.analysisChannels.add(ctx.channel());
                MapConstant.analysisMap.putIfAbsent(message.getClientId(), String.valueOf(ctx.channel().id().asShortText()));
            }
            log.info("车载客户端数量" + MapConstant.vehicleMap.size());
            log.info("数据分析端数量" + MapConstant.analysisMap.size());
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
        MapConstant.vehicleChannels.remove(ctx.channel());
        MapConstant.vehicleMap.removeValue(ctx.channel().id().asShortText());
        log.info(MapConstant.vehicleMap.toString());
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
        for (Channel channel : MapConstant.vehicleChannels) {
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
        if (MapConstant.vehicleChannels.isEmpty()) {
            log.info("没有活跃的客户端连接");
            return;
        }

        // 获取当前轮询索引
        int index = currentIndex.getAndUpdate(i -> (i + 1) % MapConstant.vehicleChannels.size());

        // 转换为数组避免并发修改问题
        Channel[] channels = MapConstant.vehicleChannels.toArray(new Channel[0]);
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
        MapConstant.vehicleChannels.forEach(channel -> {
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
