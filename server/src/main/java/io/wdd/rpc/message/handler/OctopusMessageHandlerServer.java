package io.wdd.rpc.message.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.handler.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;

@Configuration
@Slf4j(topic = "Octopus Message Handler")
public class OctopusMessageHandlerServer {


    @Resource
    ObjectMapper objectMapper;

    @RabbitHandler
    @RabbitListener(queues = "${octopus.message.octopus_to_server}"
    )
    public void HandleOctopusMessageFromAgent(Message message){

        OctopusMessage octopusMessage;

        try {
            octopusMessage = objectMapper.readValue(message.getBody(), OctopusMessage.class);
        } catch (IOException e) {
            throw new MyRuntimeException("Octopus Message Wrong !");
        }

        // Octopus Message Handler
        log.info("received from agent : {} ", octopusMessage);


        // todo what to do after  received the result

        // collect all message from agent and log to somewhere

        // 1. send some info to the specific topic name
        // 2. judge from which agent the message are
        //
    }
}
