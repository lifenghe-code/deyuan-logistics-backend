package com.monitor.transfer.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class CustomDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) {
        // 检查基本长度（至少包含type+length=5字节）
        if (in.readableBytes() < 12) {
            log.info("开始进行数据长度验证");
            return; // 等待更多数据
        }

        in.markReaderIndex(); // 标记当前位置
        try {
            log.info("开始进行数据解码");
            CustomProtocol protocol = new CustomProtocol();
            // Read message type
            protocol.setMessageType(MessageType.fromValue(in.readByte()));

            // Read client type
            protocol.setClientType(ClientType.fromValue(in.readByte()));

            // Read send time
            protocol.setSendTime(in.readLong());


            // Read client ID
            short clientIdLength = in.readShort();
            if (in.readableBytes() < clientIdLength) {
                in.resetReaderIndex();
                return;
            }
            byte[] clientIdBytes = new byte[clientIdLength];
            in.readBytes(clientIdBytes);
            protocol.setClientId(new String(clientIdBytes, StandardCharsets.UTF_8));

            // Read content length and content
            int contentLength = in.readInt();
            if (in.readableBytes() < contentLength) {
                in.resetReaderIndex();
                return;
            }
            byte[] content = new byte[contentLength];
            in.readBytes(content);

            protocol.setLength(contentLength);
            protocol.setContent(content);

            out.add(protocol);
        } catch (Exception e) {
            log.error("解码失败", e);
            in.resetReaderIndex(); // 发生异常时回退
            throw e;
        }
    }

}
