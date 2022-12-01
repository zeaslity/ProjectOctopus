package io.wdd.agent.initialization.webtest;


import io.wdd.agent.initialization.beans.AgentServerInfo;
import io.wdd.agent.initialization.bootup.CollectSystemInfo;
import io.wdd.agent.initialization.bootup.OctopusAgentInitService;
import io.wdd.common.beans.response.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("sendInfoToOctopusServer")
public class SendServerInfoController {


    @Resource
    CollectSystemInfo collectSystemInfo;

    @Resource
    OctopusAgentInitService octopusAgentInitService;

    @PostMapping("sendAgentInfo")
    public R<AgentServerInfo> send(){

        AgentServerInfo agentServerInfo = collectSystemInfo.agentServerInfo;

        octopusAgentInitService.SendInfoToServer(agentServerInfo);


        return R.ok(agentServerInfo);
    }



}
