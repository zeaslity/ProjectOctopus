package io.wdd.agent.executor;


import io.wdd.agent.config.utils.AgentCommonThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static io.wdd.agent.status.AppStatusCollector.ALL_APP_NEED_TO_MONITOR_STATUS;
import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
public class AppStatusExecutor {

    private static ArrayList<ArrayList<String>> APP_STATUS_CHECK_COMMAND;

    static {

        ArrayList<String> first = new ArrayList<>(List.of(
                "systemctl",
                "status",
                "systemd.service"
        ));

        ArrayList<String> second = new ArrayList<>(List.of(
                "grep",
                "-c",
                "active (running)"));

        ArrayList<ArrayList<String>> arrayList = new ArrayList<>();
        arrayList.add(first);
        arrayList.add(second);

        APP_STATUS_CHECK_COMMAND = arrayList;

    }

    public HashMap<String, Set<String>> checkAppStatus(boolean allAppStatus){

        // check all app status
        Map<String, List<String[]>> collect = ALL_APP_NEED_TO_MONITOR_STATUS.keySet().stream()
                .map(
                        appName -> {

                            // generate single app status callable task
                            CheckSingleAppStatusCallable singleAppStatusCallable = new CheckSingleAppStatusCallable(appName, APP_STATUS_CHECK_COMMAND);

                            // use thread pool to run the command to get the singe app status result
                            Future<String[]> appStatusFuture = AgentCommonThreadPool.pool.submit(singleAppStatusCallable);

                            return appStatusFuture;
                        }
                ).map(
                        // deal with the app status future result
                        appStatusFuture -> {
                            try {
                                return appStatusFuture.get(5, TimeUnit.SECONDS);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            } catch (TimeoutException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .collect(
                        // group the result
                        groupingBy(
                                appStatus -> appStatus[0]
                        )
                );

        // uniform the result data
        // Healthy -> [Nginx, MySQL, Xray]
        // Failure -> [Redis]
        // NotInstall -> [Docker]
        HashMap<String, Set<String>> result = new HashMap<>(16);
        collect.entrySet().stream().map(
                entry -> {
                    String status = entry.getKey();
                    Set<String> appNameSet = entry.getValue().stream().map(appStatus -> appStatus[1]).collect(Collectors.toSet());

                    result.put(status, appNameSet);
                    return 1;
                }
        ).collect(Collectors.toSet());


        return result;

    }


}
