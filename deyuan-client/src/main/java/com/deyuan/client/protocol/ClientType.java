package com.deyuan.client.protocol;

import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ClientType {
    VEHICLE(0x01,"VEHICLE"),
    ANALYSIS(0x02, "ANALYSIS"),
    SERVER(0x03, "SERVER");
    private final int value;
    private final String vehicle;

    public static final AttributeKey<ClientType> CLIENT_TYPE_KEY =
            AttributeKey.valueOf("clientType");


    public static final AttributeKey<String> CLIENT_ID =
            AttributeKey.valueOf("clientId");


    ClientType(int value, String vehicle) {
        this.value = value;
        this.vehicle = vehicle;
    }

    public String getVehicle() {
        return vehicle;
    }

    public static ClientType fromValue(byte value) {
        log.debug("解码客户端类型: {}", value); // 确认心跳包被正确解码
        for (ClientType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知客户端类型: " + value);
    }

    public int getValue() {
        return value;
    }
}

