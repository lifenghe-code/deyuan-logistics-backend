package com.monitor.transfer.constant;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MapConstant {
    public static final ChannelGroup vehicleChannels =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static final ChannelGroup analysisChannels =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 客户端 Id 对应 channelId
    public static final BidiMap<String, String> vehicleMap = new DualHashBidiMap<>();
    public static final ConcurrentMap<String, String> analysisMap = new ConcurrentHashMap<>();
    public static final ConcurrentMap<String, Integer> heartbeatMap = new ConcurrentHashMap<>();
}
