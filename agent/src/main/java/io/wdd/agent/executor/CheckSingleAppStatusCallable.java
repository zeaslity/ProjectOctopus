package io.wdd.agent.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static io.wdd.agent.status.AppStatusCollector.ALL_APP_NEED_TO_MONITOR_STATUS;

@Slf4j
public class CheckSingleAppStatusCallable implements Callable<String[]> {

    private final List<String> commandList;
    private final String appName;

    public CheckSingleAppStatusCallable(String appName, List<String> commandList) {
        this.commandList = commandList;
        this.appName = appName;
    }

    @Override
    public String[] call() throws Exception {
        String[] result = new String[2];

        // set the specific app service name
        commandList.set(2, ALL_APP_NEED_TO_MONITOR_STATUS.get(appName));
        log.debug("current app [{}] status command are => {}", appName, commandList);

        ProcessBuilder processBuilder = new ProcessBuilder(commandList);
        Process process = processBuilder.start();

        boolean waitFor = process.waitFor(20, TimeUnit.SECONDS);
        result[1] = appName;

        if (ObjectUtils.isNotEmpty(waitFor)) {
            log.debug("app status command has accomplished !");

            String appStatusCommandResult = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine();

            if (appStatusCommandResult.startsWith("1")) {
                result[0] = "Healthy";
            } else if (appStatusCommandResult.startsWith("0")) {
                result[0] = "Failure";
            } else {
                result[0] = "NotInstall";
            }
        }

        log.debug("app status check ok result is => [ {} ]", result);
        return result;
    }
}
