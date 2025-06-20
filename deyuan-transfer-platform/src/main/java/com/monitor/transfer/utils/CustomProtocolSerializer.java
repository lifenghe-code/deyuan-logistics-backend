package com.monitor.transfer.utils;

import com.monitor.transfer.protocol.CustomProtocol;
import com.monitor.transfer.protocol.MessageType;
import com.monitor.transfer.protocol.ClientType;
import java.io.*;

public class CustomProtocolSerializer {

    // 序列化方法
    public static byte[] serialize(CustomProtocol protocol) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(protocol);
            return bos.toByteArray();
        }
    }

    // 反序列化方法
    public static CustomProtocol deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        return (CustomProtocol) objectInputStream.readObject();
    }

    public static void main(String[] args) {
        try {
            // 创建测试对象
            byte[] content = "测试内容".getBytes();
            CustomProtocol original = new CustomProtocol(
                    MessageType.DATA,
                    ClientType.VEHICLE,
                    "client123",
                    content.length,
                    content
            );

            // 序列化
            byte[] serializedData = serialize(original);
            System.out.println("序列化后的字节长度: " + serializedData.length);

            // 反序列化
            CustomProtocol deserialized = deserialize(serializedData);

            // 验证结果
            System.out.println("反序列化后的对象: " + deserialized);
            System.out.println("消息类型: " + deserialized.getMessageType());
            System.out.println("客户端类型: " + deserialized.getClientType());
            System.out.println("客户端ID: " + deserialized.getClientId());
            System.out.println("内容长度: " + deserialized.getLength());
            System.out.println("内容: " + new String(deserialized.getContent()));

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
