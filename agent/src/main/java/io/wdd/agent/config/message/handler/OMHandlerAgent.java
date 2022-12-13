package io.wdd.agent.config.message.handler;

import io.wdd.agent.agent.AgentRebootUpdateService;
import io.wdd.common.beans.agent.AgentOperationMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class OMHandlerAgent extends AbstractOctopusMessageHandler {


    @Resource
    AgentRebootUpdateService agentRebootUpdateService;


    @Override
    public boolean handle(OctopusMessage octopusMessage) {
        if (!octopusMessage.getType().equals(OctopusMessageType.AGENT)) {
            return next.handle(octopusMessage);
        }

        AgentOperationMessage operationMessage = (AgentOperationMessage) octopusMessage.getContent();

        String operationName = operationMessage.getOperationName();
        if (operationName.startsWith("reb")) {
            // reboot
            agentRebootUpdateService.exAgentReboot(operationMessage);
        } else if (operationName.startsWith("upd")) {
            // update
            agentRebootUpdateService.exAgentUpdate(operationMessage);
        } else {
            // operation unknown
            log.error("Command Agent Operation Unknown! " );
        }



        return true;
    }
}
