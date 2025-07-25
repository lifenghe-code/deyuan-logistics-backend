package com.monitor.transfer.handler;


import com.monitor.transfer.constant.MapConstant;
import com.monitor.transfer.constant.MessageConstant;
import com.monitor.transfer.protocol.ClientType;
import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ChannelHandler.Sharable
@Slf4j
public class HeartbeatServerHandler extends SimpleChannelInboundHandler<CustomProtocol> {
    private static final String HT = "HEARTBEAT";
    private static final String CLIENT_ID = "000000";
    private static final CustomProtocol HEARTBEAT_BUFFER =
            new CustomProtocol(MessageType.HEARTBEAT, ClientType.VEHICLE,CLIENT_ID,HT.getBytes().length,HT.getBytes());
    


    @Override
    public void channelRead0(ChannelHandlerContext ctx, CustomProtocol message) throws Exception {
        MessageType type = message.getMessageType();

        if (type == MessageType.HEARTBEAT) {
            log.info("接收到客户端心跳包");
            String clientId = MapConstant.vehicleMap.get(ctx.channel().id().asLongText());
            if(MapConstant.heartbeatMap.containsKey(clientId)){
                MapConstant.heartbeatMap.putIfAbsent(message.getClientId(), 0);
            }

            ctx.writeAndFlush(MessageConstant.HEARTBEAT_ACK);
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
                log.warn("检测到写空闲");
                log.info(String.valueOf(MapConstant.vehicleMap));
                log.info(ctx.channel().id().asShortText());
                String clientId = MapConstant.vehicleMap.getKey(ctx.channel().id().asShortText());
                Integer count = 0;
                if(MapConstant.heartbeatMap.containsKey(clientId)){
                    count = MapConstant.heartbeatMap.get(clientId);
                }
                else{
                    count = 1;
                    MapConstant.heartbeatMap.putIfAbsent(clientId, count);
                    log.warn("发送心跳");
                    ctx.writeAndFlush(HEARTBEAT_BUFFER); // 强制发送心跳
                    return;
                }

                if (count >= 5) {
                    log.warn("id为{}的客户端掉线", clientId);
                    MapConstant.vehicleChannels.remove(ctx.channel());
                    // 根据 channelId 清理客户端
                    MapConstant.vehicleMap.forEach((k, v) -> {
                        if (ctx.channel().id().asLongText().equals(v)) {
                            MapConstant.vehicleMap.remove(k); // 使用ConcurrentHashMap的线程安全remove
                        }
                    });
                }
                else{
                    MapConstant.heartbeatMap.putIfAbsent(clientId, ++count);
                }

                log.warn("发送心跳");
                ctx.writeAndFlush(HEARTBEAT_BUFFER); // 强制发送心跳
            }
        }
    }


}

