package com.monitor.transfer.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CustomDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) {
        // 检查基本长度（至少包含type+length=5字节）
        if (in.readableBytes() < 5) {
            return; // 等待更多数据
        }

        in.markReaderIndex(); // 标记当前位置
        try {
            CustomProtocol protocol = new CustomProtocol();
            protocol.setType(MessageType.fromValue(in.readByte()));
            int length = in.readInt();

            // 检查内容是否完整
            if (in.readableBytes() < length) {
                in.resetReaderIndex(); // 回退到标记位置
                return;
            }

            byte[] content = new byte[length];
            in.readBytes(content);
            protocol.setLength(length);
            protocol.setContent(content);
            out.add(protocol);
        } catch (Exception e) {
            log.error("解码失败", e);
            in.resetReaderIndex(); // 发生异常时回退
            throw e;
        }
    }

}
