package io.wdd.agent;

import io.wdd.agent.executor.AppStatusExecutor;
import io.wdd.agent.status.AppStatusCollector;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Set;

@SpringBootTest
public class AppStatusCheckTest {

    @Resource
    AppStatusExecutor appStatusExecutor;

    @Test
    public void checkAppStatus(){
        HashMap<String, Set<String>> appStatus = appStatusExecutor.checkAppStatus(true);

        System.out.println("appStatus = " + appStatus);
    }

}
