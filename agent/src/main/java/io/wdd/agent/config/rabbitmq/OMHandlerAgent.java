package io.wdd.agent.config.rabbitmq;

import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;

public class OMHandlerAgent extends AbstractOctopusMessageHandler {
    @Override
    public boolean handle(OctopusMessage octopusMessage) {
        if (!octopusMessage.getType().equals(OctopusMessageType.AGENT)) {
            this.getNextHandler().handle(octopusMessage);
        }
        return false;
    }
}
