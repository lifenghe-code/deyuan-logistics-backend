package com.monitor.transfer.protocol;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CustomProtocol implements Serializable {
    private static final long serialVersionUID = 1L;
    private MessageType messageType; // 消息类型（HEARTBEAT/DATA）
    private ClientType clientType; // 客户端类型（Vehicle/AnalysisCenter）
    private String clientId;
    private int length;
    private byte[] content;
    private Date sendTime;

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


