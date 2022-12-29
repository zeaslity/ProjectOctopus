package io.wdd.rpc.execute.result;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import javax.annotation.Resource;
import java.time.Duration;

@Configuration
@Slf4j
@Lazy
public class RedisStreamReaderConfig {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;


    @Bean(initMethod = "start", destroyMethod = "stop")
//    @Scope(value = "prototype")
    @Lazy
    public MyStreamMessageListenerContainer redisStreamListenerContainer() {

        return new MyStreamMessageListenerContainer();
    }

    class MyStreamMessageListenerContainer {

        public String streamKey = "cccc";

        public void start() {
            log.debug("Redis Stream Reader stream key is [ {} ]", this.streamKey);

        }

        public void stop() {
           log.debug("Redis Stream Reader destroyed ! stream key is [ {} ]", this.streamKey);
        }

        public String get() {
           return this.streamKey;
        }


        public StreamMessageListenerContainer<String, MapRecord<String, String, String>> MyStreamMessageListenerContainer() {

            StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                    .builder()
                    .pollTimeout(Duration.ofSeconds(2))
                    .build();

            StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory, options);

            listenerContainer.receive(
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed()),

                    new CommandResultReader(
                            "Octopus-Server",
                            "Octopus-Group",
                            "OctopusServer")

            );

            return listenerContainer;
        }

    }

}
