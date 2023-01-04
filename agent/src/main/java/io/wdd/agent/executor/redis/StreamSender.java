package io.wdd.agent.executor.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.agent.config.beans.executor.CommandLog;
import io.wdd.agent.config.beans.executor.StreamSenderEntity;
import io.wdd.agent.config.utils.TimeUtils;
import io.wdd.agent.executor.thread.LogToArrayListCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class StreamSender {

    public static String TEST_STREAM_JAVA = "test-stream-java";

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    ObjectMapper objectMapper;

    @Resource
    LogToArrayListCache logToArrayListCache;

    private final HashMap<String, StreamSenderEntity> AllNeededStreamSender = new HashMap<>(16);

    private final ArrayList<String> cacheLogList = new ArrayList<>(256);



    @SneakyThrows
    private static Map generateFakeData() {
        String random = RandomStringUtils.random(16);
        CommandLog commandLog = new CommandLog();

        Map<String, String> map = BeanUtils.describe(commandLog);

        return map;
    }

    public void startToWaitLog(String streamKey) throws InterruptedException {

        if (!AllNeededStreamSender.containsKey(streamKey)) {

            StreamSenderEntity streamSender = StreamSenderEntity
                    .builder()
                    .cachedCommandLog(logToArrayListCache.getExecutionCmdCachedLogArrayList(streamKey))
                    .waitToSendLog(true)
                    .startIndex(0)
                    .streamKey(streamKey)
                    .build();

            AllNeededStreamSender.put(streamKey, streamSender);

        }

        TimeUnit.SECONDS.sleep(2);
        if (AllNeededStreamSender.get(streamKey).isWaitToSendLog()) {
            log.debug("stream sender wait 2 s to send message");
            AllNeededStreamSender.get(streamKey).setWaitToSendLog(false);
            batchSendLog(streamKey);
        }
    }

    public void endWaitLog(String streamKey) {

        StreamSenderEntity streamSenderEntity = AllNeededStreamSender.get(streamKey);
        streamSenderEntity.setWaitToSendLog(false);

        batchSendLog(streamKey);
    }

    public void batchSendLog(String streamKey) {
        StreamSenderEntity streamSenderEntity = AllNeededStreamSender.get(streamKey);

        //log.info("batch send log == {}", streamSenderEntity);

        ArrayList<String> cachedCommandLog = streamSenderEntity.getCachedCommandLog();

//        System.out.println("cachedCommandLog = " + cachedCommandLog);

        int startIndex = streamSenderEntity.getStartIndex();
        int endIndex = cachedCommandLog.size();

        List<String> content = cachedCommandLog.subList(startIndex, endIndex);

        // only send when cached log is not empty
        if (content.size() > 0) {
            this.send(streamKey, content);
        }

        // for next time
        streamSenderEntity.setStartIndex(endIndex);
    }

    public boolean send(String streamKey, String content) {

        HashMap<String, String> map = new HashMap<>(16);

        map.put(TimeUtils.currentTimeString(), content);

        return doSendLogToStream(streamKey, map);

    }

    private boolean send(String streamKey, List<String> content) {

        try {

            String resultContent = objectMapper.writeValueAsString(content);

            return this.send(streamKey, resultContent);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean doSendLogToStream(String streamKey, HashMap map) {

        log.info("redis stream sender message is {}", map);

        StringRecord stringRecord = StreamRecords.string(map).withStreamKey(streamKey);

        RecordId recordId = redisTemplate.opsForStream().add(stringRecord);

//            log.info("redis send recordId is {}",recordId);

        return ObjectUtils.isNotEmpty(recordId);
    }

    @SneakyThrows
    public void test() {

        RecordId recordId = null;
        if (!redisTemplate.hasKey(TEST_STREAM_JAVA)) {

            recordId = redisTemplate.opsForStream().add(TEST_STREAM_JAVA, generateFakeData());
        }

        for (int i = 0; i < 100; i++) {

            Map fakeData = generateFakeData();

            MapRecord mapRecord = StreamRecords.mapBacked(fakeData).withStreamKey(TEST_STREAM_JAVA);

            redisTemplate.opsForStream().add(mapRecord);

            TimeUnit.MILLISECONDS.sleep(200);

        }

    }

    public void clearLocalCache(String streamKey) {
        AllNeededStreamSender.remove(streamKey);
    }
}
