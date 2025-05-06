
package com.monitor.transfer.job;

import com.monitor.transfer.client.NettyClient;

public class ClientStart {
    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        client.connect("127.0.0.1", 8000);
    }
}
