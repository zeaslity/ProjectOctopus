package io.wdd.agent.config.message.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.agent.executor.CommandExecutor;
import io.wdd.agent.executor.FunctionExecutor;
import io.wdd.common.beans.executor.ExecutionMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.io.IOException;

import static io.wdd.agent.executor.function.CollectAllExecutorFunction.ALL_FUNCTION_MAP;

@Component
public class OMHandlerExecutor extends AbstractOctopusMessageHandler {

    @Resource
    CommandExecutor commandExecutor;

    @Resource
    FunctionExecutor functionExecutor;

    @Resource
    ObjectMapper objectMapper;


    @Override
    public boolean handle(OctopusMessage octopusMessage) {

        if (!octopusMessage.getType().equals(OctopusMessageType.EXECUTOR)) {
            return next.handle(octopusMessage);
        }

        try {

            ExecutionMessage executionMessage = objectMapper.readValue((String) octopusMessage.getContent(), new TypeReference<ExecutionMessage>() {
            });

            System.out.println("executionMessage = " + executionMessage);

            String executionType = executionMessage.getType();

            if (ALL_FUNCTION_MAP.containsKey(executionType)) {
                // execute the exist function
                functionExecutor.execute(executionMessage);

            } else {
                // handle command
                commandExecutor.execute(executionMessage);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
