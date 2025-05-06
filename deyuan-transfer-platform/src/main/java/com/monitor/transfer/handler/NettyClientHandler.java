package com.monitor.transfer.handler;

import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;
import com.monitor.transfer.utils.ImageUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.requests.HeartbeatResponse;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<CustomProtocol> {
    //channelActive() 会在客户端与服务器建立连接后调用。所以我们可以在这里面编写逻辑代码
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String data = "netty 活跃";
        CustomProtocol message = new CustomProtocol();
        message.setType(MessageType.DATA);
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        // byte[] bytes = ImageUtil.imageToByteArray("assets/img/spiderman.jpg");
        // byte[] bytes = ImageUtil.imageToByteArray("assets/img/img1.png");
        message.setContent(bytes);
        message.setLength(bytes.length);
        ctx.writeAndFlush(message);
    }

    // 记录已接收的消息存储
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CustomProtocol message) throws Exception {
        MessageType type = message.getType();
        if (type == MessageType.DATA) {
//            String content = new String(message.getContent(), StandardCharsets.UTF_8);
            log.info("客户端收到消息，数据长度" + message.getLength());
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
