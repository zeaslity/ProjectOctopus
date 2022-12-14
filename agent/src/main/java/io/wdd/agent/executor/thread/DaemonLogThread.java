package io.wdd.agent.executor.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.wdd.common.beans.response.R;

import java.util.concurrent.*;

//public class DaemonLogThread {
//
//    private static final ExecutorService executorService;
//
//    static {
//
//        ThreadFactory daemonLogThread = new ThreadFactoryBuilder()
//                .setDaemon(true)
//                .setNameFormat("BackendToRedisThread")
//                .setPriority(1)
//                .build();
//
//        executorService = Executors.newSingleThreadExecutor(daemonLogThread);
//
//    }
//
//    public static Future<?> start(Runnable backendToRedisStream) {
//
//        return executorService.submit(backendToRedisStream);
//    }
//
//    public static void stop(Runnable backendToRedisStream) {
//        executorService.shutdownNow();
//    }
//}
