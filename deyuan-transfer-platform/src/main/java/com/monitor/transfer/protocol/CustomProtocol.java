package com.monitor.transfer.protocol;

import lombok.Data;

@Data
public class CustomProtocol {
    private MessageType type; // 消息类型（HEARTBEAT/DATA）
    private int length;
    private byte[] content;

    public CustomProtocol(MessageType messageType) {
        this.setType(messageType);
    }

    public CustomProtocol(MessageType messageType, int length, byte[] bytes) {
        this.setType(messageType);
        this.setContent(bytes);
        this.setLength(length);
    }

    public CustomProtocol() {

    }


    // 枚举和getter/setter省略
}


