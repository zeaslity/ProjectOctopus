package io.wdd.agent.config.message.handler;

import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import org.springframework.stereotype.Component;

@Component
public class OMHandlerStatus extends AbstractOctopusMessageHandler {
    @Override
    public boolean handle(OctopusMessage octopusMessage) {

        if (!octopusMessage.getType().equals(OctopusMessageType.STATUS)) {
            return next.handle(octopusMessage);
        }

        return true;
    }
}
