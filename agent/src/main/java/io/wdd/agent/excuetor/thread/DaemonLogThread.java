package io.wdd.agent.excuetor.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class DaemonLogThread {

    private static final ExecutorService executorService;

    static {

        ThreadFactory daemonLogThread = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("DaemonLogThread")
                .setPriority(1)
                .build();

        executorService = Executors.newSingleThreadExecutor(daemonLogThread);

    }


    public static Future<?> start(Runnable logToSenderTask) {

        return executorService.submit(logToSenderTask);
    }
}
