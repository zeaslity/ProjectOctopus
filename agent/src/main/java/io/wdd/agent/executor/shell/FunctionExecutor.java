package io.wdd.agent.executor.shell;

import io.wdd.agent.executor.config.FunctionReader;
import io.wdd.agent.executor.function.CollectAllExecutorFunction;
import io.wdd.common.beans.executor.ExecutionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static io.wdd.agent.executor.function.CollectAllExecutorFunction.ALL_FUNCTION_MAP;

@Service
@Slf4j
public class FunctionExecutor {

    @Resource
    FunctionReader functionReader;

    @Resource
    CommandExecutor commandExecutor;

    // todo called by timer
    @Resource
    CollectAllExecutorFunction collectAllExecutorFunction;

    public void execute(ExecutionMessage executionMessage) {

        String resultKey = executionMessage.getResultKey();

        List<List<String>> commandList = ALL_FUNCTION_MAP.get(executionMessage.getType());

        this.execute(resultKey, commandList);

        /*Method execute = null;

        try {
            execute = Class.forName(functionShellScriptFileName).getMethod("execute", String.class);
            ReflectionUtils.invokeMethod(execute, functionShellScriptFileName, resultKey);

        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new MyRuntimeException(" Function Executor Reflection Error ! {} + {}", e.getCause(), e.getMessage());
        }*/

    }


    private void execute(String streamKey, List<List<String>> commandList) {

//        List<List<String>> commandList = functionReader.ReadFileToCommandList(functionFileName);

        log.info("all commands are {}", commandList);

        // todo modify this
        commandList.stream().map(
                command -> {
                    commandExecutor.execute(streamKey, command);
                    return 1;
                }
        ).collect(Collectors.toList());


    }
}
