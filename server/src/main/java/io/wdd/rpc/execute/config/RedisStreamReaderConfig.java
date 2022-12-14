package io.wdd.rpc.execute.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.Subscription;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
public class RedisStreamReaderConfig {

    public String streamKey;

    @Resource
    private StreamListener<String, MapRecord<String, String, String>> streamListener;


    @Bean
    public org.springframework.data.redis.stream.Subscription subscription(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {

        streamKey = "streamKey_lbzb7";

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(2))
                .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory, options);


        org.springframework.data.redis.stream.Subscription subscription = listenerContainer.receive(

                Consumer.from(streamKey, InetAddress.getLocalHost().getHostName()),

                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),

                streamListener);

        listenerContainer.start();

        return subscription;
    }

}
