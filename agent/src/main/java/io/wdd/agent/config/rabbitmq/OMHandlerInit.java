package io.wdd.agent.config.rabbitmq;

import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;



public class OMHandlerInit extends AbstractOctopusMessageHandler {

    @Override
    public boolean handle(OctopusMessage octopusMessage) {
        if (!octopusMessage.getType().equals(OctopusMessageType.INIT)) {
            this.getNextHandler().handle(octopusMessage);
        }

        // handle the init message

        return false;
    }


}
