package com.analysis.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class CustomProtocol implements Serializable {
    private MessageType messageType; // 消息类型（HEARTBEAT/DATA）
    private ClientType clientType; // 客户端类型（Vehicle/AnalysisCenter）
    private String clientId;
    private int length;
    private byte[] content;

    public CustomProtocol(MessageType messageType, int length, byte[] bytes) {
    }

    public CustomProtocol() {

    }

    public CustomProtocol(MessageType messageType, ClientType clientType, String clientId, int length, byte[] bytes) {
        this.messageType = messageType;
        this.clientType = clientType;
        this.clientId = clientId;
        this.length = length;
        this.content = bytes;
    }


    // 枚举和getter/setter省略
}


