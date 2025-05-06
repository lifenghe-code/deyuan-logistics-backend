package com.monitor.transfer.middleware;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucket {
    private final int capacity;          // 桶总容量
    private final AtomicInteger tokens;  // 当前令牌数
    private final AtomicInteger highThreshold; // 高水位线(触发Kafka)
    private final AtomicInteger lowThreshold;  // 低水位线(恢复Netty)
    private volatile boolean forceKafkaMode = false;
    private final AtomicLong lastSwitchTime = new AtomicLong(0);
    private final long coolDownPeriodMs;

    public TokenBucket(int capacity,
                                     int initialHighThreshold,
                                     int initialLowThreshold,
                                     long coolDownPeriodMs) {
        this.capacity = capacity;
        this.tokens = new AtomicInteger(capacity);
        this.highThreshold = new AtomicInteger(initialHighThreshold);
        this.lowThreshold = new AtomicInteger(initialLowThreshold);
        this.coolDownPeriodMs = coolDownPeriodMs;
    }

    public boolean shouldUseKafka() {
        int currentTokens = tokens.get();

        // 冷却期检查
        if (System.currentTimeMillis() - lastSwitchTime.get() < coolDownPeriodMs) {
            return forceKafkaMode;
        }

        // 滞回决策
        boolean newState = forceKafkaMode;
        if (!forceKafkaMode && currentTokens <= highThreshold.get()) {
            newState = true;
        } else if (forceKafkaMode && currentTokens >= lowThreshold.get()) {
            newState = false;
        }

        // 状态变更处理
        if (newState != forceKafkaMode) {
            forceKafkaMode = newState;
            lastSwitchTime.set(System.currentTimeMillis());
            if (!forceKafkaMode) {
                // 恢复Netty模式时重置令牌桶
                tokens.set(capacity);
            }
        }

        return forceKafkaMode;
    }

    public boolean tryAcquire() {
        return tokens.updateAndGet(v -> v > 0 ? v - 1 : v) >= 0;
    }

    public void refill(int newTokens) {
        tokens.updateAndGet(v -> Math.min(capacity, v + newTokens));
    }

    // 动态调整阈值
    public void updateThresholds(int high, int low) {
        if (high >= low) {
            highThreshold.set(high);
            lowThreshold.set(low);
        }
    }
}
