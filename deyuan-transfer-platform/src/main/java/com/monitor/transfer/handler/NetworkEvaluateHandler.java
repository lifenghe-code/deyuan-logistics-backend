package com.monitor.transfer.handler;


import com.monitor.transfer.protocol.ClientType;
import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.apache.kafka.common.quota.ClientQuotaEntity.CLIENT_ID;

@ChannelHandler.Sharable
@Slf4j
public class NetworkEvaluateHandler extends SimpleChannelInboundHandler<CustomProtocol> {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final String NE = "NETWORK_EVALUATE";


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, CustomProtocol message) throws Exception {
            MessageType type = message.getMessageType();
            if(!type.equals(MessageType.NETWORK_EVALUATE)){

                // 其他类型消息传递给下一个handler
                ctx.fireChannelRead(message);

            }
            else{
                log.info("接收到网络状况评估");
                sendNetworkEvaluate(ctx);
            }


    }

    private void sendNetworkEvaluate(ChannelHandlerContext ctx) {
        CustomProtocol packet = new CustomProtocol();
        packet.setContent(NE.getBytes());
        packet.setLength(NE.getBytes().length);
        packet.setClientType(ClientType.VEHICLE);
        packet.setClientId(CLIENT_ID);
        packet.setMessageType(MessageType.NETWORK_EVALUATE_ACK);
        // 更新丢包窗口（发送心跳包，是否丢包取决于是否收到Ack）
        ctx.writeAndFlush(packet);
    }





}
