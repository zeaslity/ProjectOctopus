package io.wdd.rpc.execute.result;

import io.wdd.rpc.execute.config.RedisStreamReaderConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class CommandResultReader implements StreamListener<String, MapRecord<String,String, String >> {

    @Resource
    RedisStreamReaderConfig redisStreamReaderConfig;


    @Override
    public void onMessage(MapRecord<String, String, String> message) {

        String commandLog = message.getValue().values().iterator().next();

        System.out.println("commandLog = " + commandLog);

        log.info("intend to be handled already !");

    }


    public void readFromStreamKey(String streamKey) {

        String formerKey = redisStreamReaderConfig.streamKey;
        log.info("start to change StreamReader streamKey from {} to ==> {}",formerKey, streamKey);

        redisStreamReaderConfig.streamKey = streamKey;
    }

}
