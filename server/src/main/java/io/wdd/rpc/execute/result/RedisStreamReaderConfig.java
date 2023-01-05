package io.wdd.rpc.execute.result;


import io.wdd.rpc.status.AgentStatusStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import javax.annotation.Resource;
import java.time.Duration;

@Configuration
@Slf4j
public class RedisStreamReaderConfig {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    public static final String COMMAND_RESULT_REDIS_STREAM_LISTENER_CONTAINER = "commandResultRedisStreamListenerContainer";

    public static final String AGENT_STATUS_REDIS_STREAM_LISTENER_CONTAINER = "agentStatusRedisStreamListenerContainer";

    private String streamKey = "cccc";

    public void setStreamKey(String streamKey) {
        this.streamKey = streamKey;
    }

    public String getStreamKey() {
        return streamKey;
    }

    @Bean(value = COMMAND_RESULT_REDIS_STREAM_LISTENER_CONTAINER)
    @Scope("prototype")
    @Lazy
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> commandResultRedisStreamListenerContainer(){

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(2))
                .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory, options);

        listenerContainer.receive(

                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),

                new CommandResultReader(
                        "OctopusServer",
                        streamKey,
                        "OctopusServer")

        );

        return listenerContainer;
    }

    @Bean(value = AGENT_STATUS_REDIS_STREAM_LISTENER_CONTAINER)
    @Scope("prototype")
    @Lazy
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> agentStatusRedisStreamListenerContainer(){

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(2))
                .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory, options);

        listenerContainer.receive(

                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),

                new AgentStatusStreamReader(
                        "OctopusServer",
                        "OctopusServer",
                        "OctopusServer")

        );

        return listenerContainer;
    }


}
