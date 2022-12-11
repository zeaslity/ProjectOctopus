package io.wdd.agent.executor.shell;

import io.wdd.agent.executor.config.FunctionReader;
import io.wdd.common.beans.executor.ExecutionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static io.wdd.agent.executor.function.CollectAllFunctionToServer.FUNCTION_REFLECTION;

@Service
@Slf4j
public class FunctionExecutor {

    @Resource
    FunctionReader functionReader;

    @Resource
    CommandExecutor commandExecutor;

    public void execute(ExecutionMessage executionMessage) {

        String resultKey = executionMessage.getResultKey();

        String functionShellScriptFileName = FUNCTION_REFLECTION.get(executionMessage.getType());

        this.execute(resultKey, functionShellScriptFileName);

        /*Method execute = null;

        try {
            execute = Class.forName(functionShellScriptFileName).getMethod("execute", String.class);
            ReflectionUtils.invokeMethod(execute, functionShellScriptFileName, resultKey);

        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new MyRuntimeException(" Function Executor Reflection Error ! {} + {}", e.getCause(), e.getMessage());
        }*/

    }


    private void execute(String streamKey, String functionFileName) {

        List<List<String>> commandList = functionReader.ReadFileToCommandList(functionFileName);

        log.info("all commands are {}", commandList);

        commandList.stream().map(
                command -> {
                    commandExecutor.execute(streamKey, command);
                    return 1;
                }
        ).collect(Collectors.toList());


    }
}
