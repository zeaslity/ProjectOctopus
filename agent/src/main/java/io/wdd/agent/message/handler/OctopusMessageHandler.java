package io.wdd.agent.message.handler;


import io.wdd.agent.config.rabbitmq.handler.*;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Service
public class OctopusMessageHandler {

    private AbstractOctopusMessageHandler octopusMessageHandler;


    @Resource
    OMHandlerAgent omHandlerAgent;

    @Resource
    OMHandlerExecutor omHandlerExecutor;

    @Resource
    OMHandlerInit omHandlerInit;

    @Resource
    OMHandlerStatus omHandlerStatus;

    @PostConstruct
    private void registerAllHandler() {

        AbstractOctopusMessageHandler.Builder builder = new AbstractOctopusMessageHandler.Builder();

        octopusMessageHandler = builder
                .addHandler(omHandlerExecutor)
                .addHandler(omHandlerAgent)
                .addHandler(omHandlerStatus)
                .addHandler(omHandlerInit)
                .build();

    }


    public boolean handle(OctopusMessage octopusMessage) {

        return this.octopusMessageHandler.handle(octopusMessage);
    }

}
