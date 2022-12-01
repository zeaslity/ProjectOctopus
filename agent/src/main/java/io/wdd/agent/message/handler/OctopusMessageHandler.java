package io.wdd.agent.message.handler;


import io.wdd.agent.config.rabbitmq.*;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class OctopusMessageHandler {

    private AbstractOctopusMessageHandler octopusMessageHandler;


    @PostConstruct
    private void registerAllHandler() {

        AbstractOctopusMessageHandler.Builder builder = new AbstractOctopusMessageHandler.Builder();

        octopusMessageHandler = builder
                .addHandler(new OMHandlerExecutor())
                .addHandler(new OMHandlerAgent())
                .addHandler(new OMHandlerStatus())
                .addHandler(new OMHandlerInit())
                .build();

    }


    public boolean handle(OctopusMessage octopusMessage) {

        return this.octopusMessageHandler.handle(octopusMessage);
    }

}
