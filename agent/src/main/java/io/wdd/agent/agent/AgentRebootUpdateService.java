package io.wdd.agent.agent;


import io.wdd.agent.executor.FunctionExecutor;
import io.wdd.common.beans.agent.AgentOperationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class AgentRebootUpdateService {

    @Resource
    FunctionExecutor functionExecutor;

    public void exAgentReboot(AgentOperationMessage operationMessage) {

    }

    public void exAgentUpdate(AgentOperationMessage operationMessage) {

    }
}
