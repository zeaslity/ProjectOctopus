package io.wdd.agent.config.message.handler;

import io.wdd.agent.config.beans.init.AgentServerInfo;
import io.wdd.agent.initialization.message.GenOctopusRabbitMQConnection;
import io.wdd.agent.message.ToServerMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 *  handle the agent PassThroughTopicName info
 *   1. generator the unique topic queue for agent itself
 *   2. send PassThroughTopicName successful info to the server
 */
@Lazy
@Component
@Slf4j
public class OMHandlerInit extends AbstractOctopusMessageHandler {

    @Resource
    GenOctopusRabbitMQConnection genOctopusRabbitMQConnection;

    @Resource
    ToServerMessage toServerMessage;

    @Resource
    AgentServerInfo agentServerInfo;

    @Override
    public boolean handle(OctopusMessage octopusMessage) {
        if (!octopusMessage.getType().equals(OctopusMessageType.INIT)) {
            return next.handle(octopusMessage);
        }

        // handle the PassThroughTopicName message
        // 1. generator the unique topic queue for agent itself
        // 1.1 initial the specific topic queue listener
        genOctopusRabbitMQConnection.ManualGenerate(octopusMessage);


        // 2. send PassThroughTopicName successful info to the server
        String success = String.format("[Octopus Agent] - [ %s ] has successfully PassThroughTopicName with server [ %s ] !", agentServerInfo.getServerName(), octopusMessage.getUuid());

        octopusMessage.setResult(success);
//        log.info(success);

        toServerMessage.send(octopusMessage);

        return true;
    }


}
