package io.wdd.agent.excuetor.redis;


import io.wdd.agent.config.beans.executor.CommandLog;
import lombok.SneakyThrows;
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
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
public class StreamSender {

    @Resource
    RedisTemplate redisTemplate;


    private static ByteBuffer currentTimeByteBuffer(){

        byte[] timeBytes = LocalDateTime.now(ZoneId.of("UTC+8")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).getBytes(StandardCharsets.UTF_8);

        return ByteBuffer.wrap(timeBytes);
    }

    private static String currentTimeString(){

        return LocalDateTime.now(ZoneId.of("UTC+8")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    public static String TEST_STREAM_JAVA = "test-stream-java";


    public boolean send(String streamKey, String content){

        CommandLog commandLog = new CommandLog(currentTimeString(), content);
        Map<String, String> map = null;
        try {
            map = BeanUtils.describe(commandLog);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        StringRecord stringRecord = StreamRecords.string(map).withStreamKey(streamKey);

        RecordId recordId = redisTemplate.opsForStream().add(stringRecord);

        return ObjectUtils.isNotEmpty(recordId);

    }



    @SneakyThrows
    public void test(){

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

    @SneakyThrows
    private static Map generateFakeData() {
        String random = RandomStringUtils.random(16);
        CommandLog commandLog = new CommandLog();

        Map<String, String> map = BeanUtils.describe(commandLog);

        return map;
    }

}
