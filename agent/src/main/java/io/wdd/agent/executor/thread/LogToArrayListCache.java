package io.wdd.agent.executor.thread;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * utils to cache store the command execution logs
 */
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

        ArrayList<String> commandCachedLog = this.getExecutionCmdCachedLogArrayList(streamKey);

//        log.info(String.valueOf(commandCachedLog));

        // read from input stream and store to the cacheArrayList
        new BufferedReader(new InputStreamReader(commandLogStream))
                .lines()
                .forEach(
                        commandCachedLog::add
                );

        log.info("current streamKey is {} and CacheLog is {}", streamKey, commandCachedLog);
    }

    public ArrayList<String> getExecutionCmdCachedLogArrayList(String streamKey) {

        int keyToIndex = this.hashStreamKeyToCachedArrayListIndex(streamKey);

        return CachedCommandLog.get(keyToIndex);
    }

    private int hashStreamKeyToCachedArrayListIndex(String streamKey) {

        int size = CachedCommandLog.size();

        return Math.abs(streamKey.hashCode() % size);
    }

}
