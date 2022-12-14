package io.wdd.agent.executor.thread;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class LogToArrayListCache {

    // concurrent command execute logs
    public static List<ArrayList<String>> CachedCommandLog = List.of(
            new ArrayList<>(256),
            new ArrayList<>(256),
            new ArrayList<>(256),
            new ArrayList<>(256),
            new ArrayList<>(256)
    );

    public void cacheLog(String streamKey, InputStream commandLogStream) {

        ArrayList<String> commandCachedLog = this.getCommandCachedLog(streamKey);

//        log.info(String.valueOf(commandCachedLog));

        new BufferedReader(new InputStreamReader(commandLogStream))
                .lines()
                .forEach(
                        commandCachedLog::add
                );

        log.info("current streamKey is {} and CacheLog is {}", streamKey, commandCachedLog);
    }

    public ArrayList<String> getCommandCachedLog(String streamKey) {

        int keyToIndex = this.hashStreamKeyToIndex(streamKey);

        return CachedCommandLog.get(keyToIndex);
    }

    private int hashStreamKeyToIndex(String streamKey) {

        int size = CachedCommandLog.size();

        return Math.abs(streamKey.hashCode() % size);
    }

}
