package com.deyuan.client.handler;

import com.deyuan.client.constant.ClientConstant;
import com.deyuan.client.protocol.ClientType;
import com.deyuan.client.protocol.CustomProtocol;
import com.deyuan.client.protocol.MessageType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<CustomProtocol> {
    //channelActive() 会在客户端与服务器建立连接后调用。所以我们可以在这里面编写逻辑代码
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String data = "netty 活跃";
        CustomProtocol message = new CustomProtocol();
        message.setMessageType(MessageType.AUTH);
        message.setClientType(ClientType.VEHICLE);
        message.setClientId(ClientConstant.CLIENT_ID);
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        message.setContent(bytes);
        message.setLength(bytes.length);
        ctx.writeAndFlush(message);
    }

    // 记录已接收的消息存储
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustomProtocol message) throws Exception {
        MessageType type = message.getMessageType();
        if (type == MessageType.DATA) {
//            String content = new String(message.getContent(), StandardCharsets.UTF_8);
            log.info("客户端收到消息，数据长度" + message.getLength());
        }
        if (type == MessageType.DATA_ACK) {
//            String content = new String(message.getContent(), StandardCharsets.UTF_8);
            log.info("客户端收到数据确认信息");
        }
        else {
            // 其他类型消息传递给下一个handler
            ctx.fireChannelRead(message);
        }
//        else if(type==MessageType.HEARTBEAT)  {
//            log.info("客户端接收到心跳检测信号");
//
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
