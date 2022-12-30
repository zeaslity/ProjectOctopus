package io.wdd.rpc.execute.result;

import com.fasterxml.jackson.core.JsonProcessingException;
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

        String value = (String) message.getValue().values().toArray()[0];


        ObjectMapper objectMapper = new ObjectMapper();
        try {

            System.out.println("streamKey = " + streamKey);
            System.out.println("messageId = " + messageId);
            System.out.println("key = " + key);
            System.out.println("value = " + value);

            ArrayList<String>commandResultList = objectMapper.readValue(value, ArrayList.class);
            commandResultList.stream().forEach(
                    System.out::println
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        log.info("intend to be handled already !");

    }


}
