package com.monitor.transfer.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

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
            out.writeByte(msg.getType().getValue());
            out.writeInt(msg.getLength());
            out.writeBytes(msg.getContent());
        } catch (Exception e) {
            log.error("编码异常", e);
            throw e;
        }
    }
}