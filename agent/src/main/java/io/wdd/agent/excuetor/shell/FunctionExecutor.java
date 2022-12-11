package io.wdd.agent.excuetor.shell;

import io.wdd.common.beans.executor.ExecutionMessage;
import io.wdd.common.handler.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static io.wdd.agent.excuetor.function.CollectAllFunctionToServer.FUNCTION_REFLECTION;

@Service
@Slf4j
public class FunctionExecutor {

    public void execute(ExecutionMessage executionMessage) {

        String resultKey = executionMessage.getResultKey();

        String functionClassPath = FUNCTION_REFLECTION.get(executionMessage.getContend());

        Method execute = null;

        try {
            execute = Class.forName(functionClassPath).getMethod("execute", String.class);
            ReflectionUtils.invokeMethod(execute, functionClassPath, resultKey);

        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new MyRuntimeException(" Function Executor Reflection Error ! {} + {}", e.getCause(), e.getMessage());
        }

    }
}
