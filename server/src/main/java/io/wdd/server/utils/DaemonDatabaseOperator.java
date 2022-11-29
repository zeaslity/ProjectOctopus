package io.wdd.server.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.wdd.server.beans.vo.ServerInfoVO;
import io.wdd.server.coreService.CoreServerService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * The type Daemon database operator.
 */
@Component
public class DaemonDatabaseOperator {

    /**
     * The Core server service.
     */
    @Resource
    CoreServerService coreServerService;

    private ThreadFactory threadFactory;

    private ExecutorService fixedThreadPool;

    /**
     * Save init octopus agent info boolean.
     *
     * @param serverInfoVO the server info vo
     * @return the result
     */
    public boolean saveInitOctopusAgentInfo(ServerInfoVO serverInfoVO) {

        return coreServerService.serverCreate(serverInfoVO);
    }

    @PostConstruct
    private void buildDaemonDatabaseThread() {

        threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("database-daemon")
                .setPriority(10)
                .build();

        fixedThreadPool = Executors.newFixedThreadPool(1, threadFactory);
    }
}
