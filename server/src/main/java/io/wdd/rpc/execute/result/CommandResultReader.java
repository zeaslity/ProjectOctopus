package io.wdd.rpc.execute.result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Slf4j
public class CommandResultReader implements StreamListener<String, MapRecord<String,String, String >> {

    // https://medium.com/nerd-for-tech/event-driven-architecture-with-redis-streams-using-spring-boot-a81a1c9a4cde

    //https://segmentfault.com/a/1190000040946712

    //https://docs.spring.io/spring-data/redis/docs/2.5.5/reference/html/#redis.streams.receive.containers

    /**
     * 消费者类型：独立消费、消费组消费
     */
    private String consumerType;
    /**
     * 消费组
     */
    private String group;
    /**
     * 消费组中的某个消费者
     */
    private String consumerName;


    public CommandResultReader(String consumerType, String group, String consumerName) {
        this.consumerType = consumerType;
        this.group = group;
        this.consumerName = consumerName;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {

        String streamKey = message.getStream();
        RecordId messageId = message.getId();
        String key = (String) message.getValue().keySet().toArray()[0];
        String value = message.getValue().get(key);


        log.info("Octopus Agent [ {} ] execution of [ {} ] Time is [ {} ] stream recordId is [{}]", streamKey, key, key, messageId);

        // print to console
        printPrettyDeserializedCommandResult(value);

        // log to somewhere

    }

    private void printPrettyDeserializedCommandResult(String valueString){

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        try {

            String tmp = objectMapper.readValue(valueString, new TypeReference<String>() {
            });

            List<String> stringList = objectMapper.readValue(tmp, new TypeReference<List<String>>() {
            });

            stringList.stream().forEach(
                    System.out::println
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}
