package io.wdd.agent.message;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.agent.config.beans.init.AgentServerInfo;
import io.wdd.agent.initialization.message.InitRabbitMQConnector;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.handler.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
@Slf4j(topic = "To Octopus Server Message")
public class ToServerMessage {

    @Resource
    InitRabbitMQConnector initRabbitMqConnector;

    @Resource
    ObjectMapper objectMapper;

    @Resource
    RabbitTemplate rabbitTemplate;



    public boolean send(OctopusMessage octopusMessage) {

        octopusMessage.setAc_time(LocalDateTime.now());

        // send to Queue -- InitToServer

        log.info("send Message to Server = {}", octopusMessage);

        try {

            rabbitTemplate.convertAndSend(
                    initRabbitMqConnector.OCTOPUS_EXCHANGE,
                    initRabbitMqConnector.OCTOPUS_TO_SERVER,
                    objectMapper.writeValueAsBytes(octopusMessage)
            );


        } catch (JsonProcessingException e) {

            log.error("Failed to send message to Serv er ! = {}", octopusMessage);
            throw new MyRuntimeException(e);
        }


        return true;
    }


    public void sendInitInfo(AgentServerInfo agentServerInfo, String defaultInitRegisterTimeOut) {

        // set PassThroughTopicName agent register ttl
        InitMessagePostProcessor initMessagePostProcessor = new InitMessagePostProcessor(defaultInitRegisterTimeOut);

        log.info("send INIT AgentServerInfo to Server = {}", agentServerInfo);

        // send the register server info to EXCHANGE:INIT_EXCHANGE QUEUE: init_to_server
        try {
            rabbitTemplate.convertAndSend(initRabbitMqConnector.INIT_EXCHANGE, initRabbitMqConnector.INIT_TO_SERVER_KEY, objectMapper.writeValueAsBytes(agentServerInfo), initMessagePostProcessor);
        } catch (JsonProcessingException e) {
            log.error("Failed to send INIT message to Server ! = {}", agentServerInfo);
            throw new RuntimeException(e);
        }

    }

    private static class InitMessagePostProcessor implements MessagePostProcessor {

        private final String initMessageTTL;

//        public InitMessagePostProcessor(Long initMessageTTL) {
//            this.initMessageTTL = String.valueOf(initMessageTTL);
//        }

        public InitMessagePostProcessor(String initMessageTTL) {
            this.initMessageTTL = initMessageTTL;
        }

        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            // set PassThroughTopicName register expiration time
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setExpiration(initMessageTTL);
            return message;
        }
    }
}
