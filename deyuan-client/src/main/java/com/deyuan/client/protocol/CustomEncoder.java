package com.deyuan.client.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 将CustomProtocol编码为ByteBuf
 */
@Slf4j
public class CustomEncoder extends MessageToByteEncoder<CustomProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx,
                          CustomProtocol msg,
                          ByteBuf out) {
        try {
            log.info("进行编码");
            // Write message fields
            out.writeByte(msg.getMessageType().getValue());
            out.writeByte(msg.getClientType().getValue());

            // Write client ID (with length prefix)
            byte[] clientIdBytes = msg.getClientId().getBytes(StandardCharsets.UTF_8);
            out.writeShort(clientIdBytes.length);
            out.writeBytes(clientIdBytes);

            // Write content
            out.writeInt(msg.getLength());
            out.writeBytes(msg.getContent());
        } catch (Exception e) {
            log.error("编码异常", e);
            throw e;
        }
    }
}