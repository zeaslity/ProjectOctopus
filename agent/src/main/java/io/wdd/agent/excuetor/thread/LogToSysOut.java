package io.wdd.agent.excuetor.thread;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class LogToSysOut implements Runnable {

    private final InputStream inputStream;
    private final Consumer<String> consumer;

    public LogToSysOut(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines()
                .map(
                        String::valueOf
                )
                .forEach(consumer);
    }
}
