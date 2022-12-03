package io.wdd.agent.config.message.handler;

import io.wdd.common.beans.rabbitmq.OctopusMessage;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class OMHandlerBlackHole extends AbstractOctopusMessageHandler {
    @Override
    public boolean handle(OctopusMessage octopusMessage) {


        log.error("Octopus Message Handle error !   No Handler Find !");

        return false;
    }
}
