package io.wdd.agent.executor.shell;

import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import io.wdd.agent.executor.config.FunctionReader;
import io.wdd.agent.executor.function.CollectAllExecutorFunction;
import io.wdd.common.beans.executor.ExecutionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static io.wdd.agent.executor.function.CollectAllExecutorFunction.ALL_FUNCTION_MAP;
import static io.wdd.agent.executor.function.CollectAllExecutorFunction.NacosConfigService;

@Service
@Slf4j
public class FunctionExecutor {

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

        Iterator<List<String>> iterator = commandList.iterator();

        while (iterator.hasNext()) {

            int execute = commandExecutor.execute(streamKey, iterator.next());

            if (execute != 0) {
                log.error("command list execute failed !");
                break;
            }

        }
    }


    @Bean
    private void daemonListenToNacosFunctions(){

        // add listener to listen to the real-time change of the Function Shell Scripts
        try {
            NacosConfigService.addListener(collectAllExecutorFunction.dataId + "." + collectAllExecutorFunction.fileExtension, collectAllExecutorFunction.group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String s) {

                    log.info("detected nacos function shell update ! {}", s);

                    collectAllExecutorFunction.parseNacosFunctionYamlToMap(s);

                }
            });

        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

}
