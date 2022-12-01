package io.wdd.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.handler.MyRuntimeException;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MessageUtils {

    @Autowired
    ObjectMapper objectMapper;

    public static OctopusMessage convert(Message message) {

        OctopusMessage octopusMessage;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            octopusMessage = objectMapper.readValue(message.getBody(), OctopusMessage.class);

        } catch (IOException e) {
            throw new MyRuntimeException(e.getMessage());
        }

        return octopusMessage;
    }
}
