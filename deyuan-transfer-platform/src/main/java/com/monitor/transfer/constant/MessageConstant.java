package com.monitor.transfer.constant;

import com.monitor.transfer.protocol.ClientType;
import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;

import static org.apache.kafka.common.quota.ClientQuotaEntity.CLIENT_ID;

public class MessageConstant {
    private static final String HT = "HEARTBEAT";
    private static final String RM = "HEARTBEAT";
    private static final String CLIENT_ID = "000000";
    public static final CustomProtocol HEARTBEAT_MESSSAGE =
            new CustomProtocol(MessageType.HEARTBEAT, ClientType.VEHICLE,CLIENT_ID,HT.getBytes().length,HT.getBytes());

    public static final CustomProtocol REPLY_MESSAGE =
            new CustomProtocol(MessageType.DATA_ACK, ClientType.SERVER,CLIENT_ID,RM.getBytes().length,RM.getBytes());

    public static final CustomProtocol HEARTBEAT_ACK =
            new CustomProtocol(MessageType.HEARTBEAT_ACK, ClientType.VEHICLE,CLIENT_ID,HT.getBytes().length,HT.getBytes());
}
