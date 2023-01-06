package io.wdd.agent.executor;

import io.wdd.agent.executor.config.CommandPipelineBuilder;
import io.wdd.common.beans.status.AppStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static io.wdd.agent.executor.AppStatusExecutor.ALL_APP_NEED_TO_MONITOR_STATUS;


@Slf4j
public class CheckSingleAppStatusCallable implements Callable<String[]> {

    private final ArrayList<ArrayList<String>> commandList;
    private final String appName;

    public CheckSingleAppStatusCallable(String appName, ArrayList<ArrayList<String>> commandList) {
        this.commandList = commandList;
        this.appName = appName;
    }

    @Override
    public String[] call() throws Exception {
        String[] result = new String[2];

        // appName is fixed here
        result[1] = appName;

        // set the specific app service name
        commandList.get(0).set(2, ALL_APP_NEED_TO_MONITOR_STATUS.get(appName));
        log.debug("current app [{}] status command are => {}", appName, commandList);

        // judge if the app is existed !
        ProcessBuilder processBuilder = new ProcessBuilder(commandList.get(0));
        Process process = processBuilder.start();
        boolean waitFor = process.waitFor(5, TimeUnit.SECONDS);

        if (ObjectUtils.isNotEmpty(waitFor)) {
            // judge by error stream
            String error = new BufferedReader(new InputStreamReader(process.getErrorStream())).readLine();
            if (StringUtils.isNotEmpty(error)) {
                // app not existed!
                log.debug("app not installed !");
                result[0] = AppStatusEnum.NOT_INSTALL.getName();
            } else {
                log.debug("app existed! and then check the running status !");
                // app existed! and then check the running status !
                // get result from the command pipeline builder
                List<String> resultList = CommandPipelineBuilder.runGetResult(commandList);

                if (ObjectUtils.isNotEmpty(resultList)) {
                    log.debug("app status command has accomplished !");

                    String appStatusCommandResult = resultList.get(0);

                    Assert.notNull(appStatusCommandResult, "app status command result is null !");

                    if (appStatusCommandResult.startsWith("1")) {
                        result[0] = AppStatusEnum.HEALTHY.getName();
                    } else if (appStatusCommandResult.startsWith("0")) {
                        result[0] = AppStatusEnum.FAILURE.getName();
                    } else {
                        result[0] = AppStatusEnum.NOT_INSTALL.getName();
                    }
                }
                log.debug("app [ {} ] status check result is => {} ", appName, resultList);
            }
        }

        return result;
    }
}
