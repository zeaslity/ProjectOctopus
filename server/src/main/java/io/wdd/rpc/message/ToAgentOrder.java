package io.wdd.rpc.message;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import io.wdd.common.handler.MyRuntimeException;
import io.wdd.rpc.init.FromServerMessageBinding;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.amqp.RabbitHealthIndicator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * adaptor
 *  provide override method to convert Object and send to rabbitmq
 */
@Component
public class ToAgentOrder {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    FromServerMessageBinding fromServerMessageBinding;

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

        rabbitTemplate.convertAndSend(fromServerMessageBinding.INIT_EXCHANGE, fromServerMessageBinding.INIT_FROM_SERVER_KEY, writeData(message));

    }

    @SneakyThrows
    private byte[] writeData(Object data){
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsBytes(data);
    }

}
