package io.wdd.rpc.message;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import io.wdd.common.handler.MyRuntimeException;
import io.wdd.rpc.init.FromServerMessageBinding;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * adaptor
 *  provide override method to convert Object and send to rabbitmq
 */
@Component
@Slf4j(topic = "order to agent ")
public class ToAgentOrder {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    FromServerMessageBinding fromServerMessageBinding;


    @Resource
    ObjectMapper objectMapper;

    /**
     *
     *  send to Queue -- InitFromServer
     *
     * @param message octopus message
     */
    public void send(OctopusMessage message){

        // only accept INIT type message
        if (!OctopusMessageType.INIT.equals(message.getType())) {
            throw new MyRuntimeException("To Agent Order method usage wrong !");
        }

        // send to Queue -- InitFromServer
        log.info("send INIT OrderCommand to Agent = {}", message);

        rabbitTemplate.convertAndSend(fromServerMessageBinding.INIT_EXCHANGE, fromServerMessageBinding.INIT_FROM_SERVER_KEY, writeData(message));

    }

    @SneakyThrows
    private byte[] writeData(Object data){

        return objectMapper.writeValueAsBytes(data);
    }

}