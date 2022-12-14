package io.wdd.rpc.message.sender;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import io.wdd.common.handler.MyRuntimeException;
import io.wdd.rpc.init.InitRabbitMQConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * adaptor
 * provide override method to convert Object and send to rabbitmq
 */
@Component
@Slf4j(topic = "Send Message To Octopus Agent ")
public class ToAgentMessageSender {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    InitRabbitMQConfig initRabbitMQConfig;

    @Resource
    ObjectMapper objectMapper;

    /**
     * send to Queue -- InitFromServer
     *
     * @param message octopus message
     */
    public void sendINIT(OctopusMessage message) {

        // only accept INIT type message
        if (!OctopusMessageType.INIT.equals(message.getType())) {
            throw new MyRuntimeException("To Agent Order method usage wrong !");
        }

        // send to Queue -- InitFromServer
        log.info("send INIT OrderCommand to Agent = {}", message);

        rabbitTemplate.convertAndSend(initRabbitMQConfig.INIT_EXCHANGE, initRabbitMQConfig.INIT_FROM_SERVER_KEY, writeData(message));

    }


    public void send(OctopusMessage octopusMessage) {

        log.info("OctopusMessage {} send to agent {}", octopusMessage, octopusMessage.getUuid());

        rabbitTemplate.convertAndSend(
                initRabbitMQConfig.OCTOPUS_EXCHANGE,
                octopusMessage.getUuid() + "*",
                writeData(octopusMessage));

    }


    public void send(List<OctopusMessage> octopusMessageList) {

        octopusMessageList.stream().forEach(
                octopusMessage -> {
                    this.send(octopusMessage);
                }
        );

    }

    @SneakyThrows
    private byte[] writeData(Object data) {

        return objectMapper.writeValueAsBytes(data);
    }

}
