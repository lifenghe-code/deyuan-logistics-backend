package com.analysis.manager;

import cn.hutool.core.thread.NamedThreadFactory;

import java.util.concurrent.*;

public class ComputeThreadPool {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    // 静态线程池，启动时就初始化好
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            CPU_COUNT,
            CPU_COUNT,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1000),
            new NamedThreadFactory("compute", true),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    // 获取线程池
    public static ThreadPoolExecutor getInstance() {
        return EXECUTOR;
    }

    // 提交任务的快捷方式
    public static void submit(Runnable task) {
        EXECUTOR.submit(task);
    }

    // 优雅关闭（可选）
    public static void shutdown() {
        EXECUTOR.shutdown();
    }
}

