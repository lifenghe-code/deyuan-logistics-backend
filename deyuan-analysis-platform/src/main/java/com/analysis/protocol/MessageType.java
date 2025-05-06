package com.analysis.protocol;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义协议消息类型
 */
@Slf4j
public enum MessageType {
    HEARTBEAT(0x01),   // 心跳消息
    AUTH(0x02),        // 认证消息
    DATA(0x03),        // 业务数据
    ACK(0x04);         // 确认应答

    private final int value;

    MessageType(int value) {
        this.value = (byte) value;
    }

    public int getValue() {
        return value;
    }

    public static MessageType fromValue(int value) {
        log.debug("解码消息类型: {}", value); // 确认心跳包被正确解码
        for (MessageType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知消息类型: " + value);
    }
}
