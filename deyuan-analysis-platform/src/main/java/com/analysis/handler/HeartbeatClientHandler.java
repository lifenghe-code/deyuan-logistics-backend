package com.analysis.handler;


import com.analysis.protocol.CustomProtocol;
import com.analysis.protocol.MessageType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
@ChannelHandler.Sharable
@Slf4j
public class HeartbeatClientHandler extends SimpleChannelInboundHandler<CustomProtocol> {
    private static final String HT = "HEARTBEAT";
    private static final CustomProtocol HEARTBEAT_BUFFER =
            new CustomProtocol(MessageType.HEARTBEAT,HT.getBytes().length,HT.getBytes());
    @Override
    public void channelRead0(ChannelHandlerContext ctx, CustomProtocol message) throws Exception {
        MessageType type = message.getMessageType();
        if (type == MessageType.HEARTBEAT) {
            log.info("接收到服务端心跳包");
            //ctx.writeAndFlush(HEARTBEAT_BUFFER);
        } else {
            // 其他类型消息传递给下一个handler
            ctx.fireChannelRead(message);
        }
//        log.info(msg.toString());
//        if (msg instanceof CustomProtocol && ((CustomProtocol) msg).getType() == MessageType.HEARTBEAT) {
//            log.info("接收到服务端心跳包");
//            ctx.writeAndFlush(HEARTBEAT_BUFFER);
//        } else {
//            super.channelRead(ctx, msg);
//        }
    }
}
