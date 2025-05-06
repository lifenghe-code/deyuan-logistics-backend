package com.monitor.transfer.handler;


import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class HeartbeatServerHandler extends SimpleChannelInboundHandler<CustomProtocol> {
    private static final String HT = "HEARTBEAT";
    private static final CustomProtocol HEARTBEAT_BUFFER =
            new CustomProtocol(MessageType.HEARTBEAT,HT.getBytes().length,HT.getBytes());
    @Override
    public void channelRead0(ChannelHandlerContext ctx, CustomProtocol message) throws Exception {
        MessageType type = message.getType();

        if (type == MessageType.HEARTBEAT) {
            log.info("接收到客户端心跳包");
        } else {
            // 其他类型消息传递给下一个handler
            ctx.fireChannelRead(message);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.WRITER_IDLE) {
                log.warn("检测到写空闲，发送心跳...");
                ctx.writeAndFlush(HEARTBEAT_BUFFER); // 强制发送心跳
            }
        }
    }
}

