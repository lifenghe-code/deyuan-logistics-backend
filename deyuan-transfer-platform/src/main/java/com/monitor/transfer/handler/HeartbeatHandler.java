package com.monitor.transfer.handler;

import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private static final String HT = "HEARTBEAT";
    private static final CustomProtocol HEARTBEAT_BUFFER =
            new CustomProtocol(MessageType.HEARTBEAT,HT.getBytes().length,HT.getBytes());
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info(msg.toString());
        if (msg instanceof CustomProtocol  && ((CustomProtocol) msg).getMessageType() == MessageType.HEARTBEAT) {
            log.info("接收到心跳包");
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            log.info("发送心跳检测");
//            String data = "netty 活跃";
//            CustomProtocol message = new CustomProtocol();
//            message.setType(MessageType.DATA);
//            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
//            message.setContent(bytes);
//            message.setLength(bytes.length);
            ctx.writeAndFlush(HEARTBEAT_BUFFER);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
