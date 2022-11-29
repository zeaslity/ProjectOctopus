package io.wdd.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.handler.MyRuntimeException;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MessageUtils {

    public static OctopusMessage convert(Message message) {

        ObjectMapper objectMapper = new ObjectMapper();

        OctopusMessage octopusMessage;

        try {
            octopusMessage = objectMapper.readValue(message.getBody(), OctopusMessage.class);

        } catch (IOException e) {
            throw new MyRuntimeException(e.getMessage());
        }

        return octopusMessage;
    }
}
