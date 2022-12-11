package io.wdd.agent.config.message.handler;

import io.wdd.agent.excuetor.shell.CommandExecutor;
import io.wdd.agent.excuetor.shell.FunctionExecutor;
import io.wdd.common.beans.executor.ExecutionMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static io.wdd.agent.excuetor.function.CollectAllFunctionToServer.ALL_FUNCTIONS;

@Component
public class OMHandlerExecutor extends AbstractOctopusMessageHandler {

    @Resource
    CommandExecutor commandExecutor;

    @Resource
    FunctionExecutor functionExecutor;

    @Override
    public boolean handle(OctopusMessage octopusMessage) {

        if (!octopusMessage.getType().equals(OctopusMessageType.EXECUTOR)) {
            return next.handle(octopusMessage);
        }

        ExecutionMessage executionMessage = (ExecutionMessage) octopusMessage.getContent();
        String executionType = executionMessage.getType();

        if (ALL_FUNCTIONS.contains(executionType)) {
            // execute the exist function
            functionExecutor.execute(executionMessage);

        } else {
            // handle command
            commandExecutor.execute(executionMessage);
        }

        return true;
    }
}
