package com.monitor.transfer.job;


import com.monitor.transfer.server.NettyServer;

public class ServerStart {
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(8000);
        // 本地加载数据，模拟接口获取数据
    }
}
