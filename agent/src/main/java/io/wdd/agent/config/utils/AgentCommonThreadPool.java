package io.wdd.agent.config.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class AgentCommonThreadPool {

    public static ExecutorService pool;


    static {

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("agent-pool-%d")
                .setDaemon(false)
                .build();


        // construct the thread pool
        pool = new ThreadPoolExecutor(
                5,
                10,
                500,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(8,true),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );

    }
}
