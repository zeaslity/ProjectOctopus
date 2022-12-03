package io.wdd.agent.excuetor.redis;


import io.wdd.agent.config.beans.executor.CommandLog;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.HashMapper;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
public class StreamSenderTest {

    @Resource
    RedisTemplate redisTemplate;

    public static String TEST_STREAM_JAVA = "test-stream-java";

    @SneakyThrows
    public void test(){

        HashMapper hashMapper = redisTemplate.opsForStream().getHashMapper(CommandLog.class);

        RecordId recordId = null;
        if (!redisTemplate.hasKey(TEST_STREAM_JAVA)) {

            recordId = redisTemplate.opsForStream().add(TEST_STREAM_JAVA, generateFakeData(hashMapper));
        }

        for (int i = 0; i < 100; i++) {

            Map fakeData = generateFakeData(hashMapper);

            MapRecord mapRecord = StreamRecords.mapBacked(fakeData).withId(recordId).withStreamKey(TEST_STREAM_JAVA);


            recordId = redisTemplate.opsForStream(hashMapper).add(mapRecord);

            TimeUnit.MILLISECONDS.sleep(200);

        }



    }

    private static Map generateFakeData(HashMapper hashMapper) {
        String random = RandomStringUtils.random(16);
        CommandLog commandLog = new CommandLog(LocalDateTime.now(), random);
        Map map = hashMapper.toHash(commandLog);
        return map;
    }

}
