package io.wdd.rpc.controller;

import io.wdd.common.beans.response.R;
import io.wdd.rpc.execute.result.CreateStreamReader;
import io.wdd.rpc.execute.service.CoreExecutionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;

import static io.wdd.rpc.execute.result.RedisStreamReaderConfig.AGENT_STATUS_REDIS_STREAM_LISTENER_CONTAINER;
import static io.wdd.rpc.execute.result.RedisStreamReaderConfig.COMMAND_RESULT_REDIS_STREAM_LISTENER_CONTAINER;

@RestController
@RequestMapping("octopus/server/executor")
public class ExecutionController {

    @Resource
    CoreExecutionService coreExecutionService;

    @Resource
    CreateStreamReader createStreamReader;


    @PostMapping("command")
    public R<String> patchCommandToAgent(
            @RequestParam(value = "topicName") String topicName,
            @RequestParam(value = "commandList", required = false) @Nullable List<String> commandList,
            @RequestParam(value = "type", required = false) @Nullable String type
    ) {

        String streamKey = "";

        if (StringUtils.isEmpty(type)) {
            streamKey = coreExecutionService.SendCommandToAgent(topicName, commandList);
        } else {
            streamKey = coreExecutionService.SendCommandToAgent(topicName, type, commandList);
        }

        return R.ok(streamKey);
    }

    @PostMapping("/stream")
    public void GetCommandLog(
            @RequestParam(value = "streamKey") String streamKey
    ) {

        createStreamReader.registerStreamReader(COMMAND_RESULT_REDIS_STREAM_LISTENER_CONTAINER ,streamKey);
    }


    @PostMapping("/agentStatusStream")
    public void getAgentStatus(
            @RequestParam(value = "streamKey") String streamKey
    ) {

        createStreamReader.registerStreamReader(AGENT_STATUS_REDIS_STREAM_LISTENER_CONTAINER ,streamKey);

    }


    @PostMapping("/agentUpdate")
    public void AgentUpdate(
            @RequestParam(value = "agentTopicName") String agentTopicName
    ) {



    }


}
