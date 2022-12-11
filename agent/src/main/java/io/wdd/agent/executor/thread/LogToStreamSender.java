package io.wdd.agent.executor.thread;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;

public class LogToStreamSender implements Runnable {

    private final InputStream contentInputStream;
    private final String streamKey;
    private final BiConsumer<String, String> biConsumer;

    public LogToStreamSender(String streamKey, InputStream contentInputStream, BiConsumer<String, String> biConsumer) {
        this.contentInputStream = contentInputStream;
        this.biConsumer = biConsumer;
        this.streamKey = streamKey;
    }

    @Override
    public void run() {
        new BufferedReader(new InputStreamReader(contentInputStream)).lines()
                .map(
                        String::valueOf
                ).map(
                        lineStr -> {
                            biConsumer.accept(streamKey, lineStr);
                            return lineStr;
                        }
                ).forEach(System.out::println);
    }
}
