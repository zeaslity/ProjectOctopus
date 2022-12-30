package io.wdd.rpc.execute.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.common.beans.executor.ExecutionMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import io.wdd.rpc.execute.result.CreateStreamReader;
import io.wdd.rpc.message.sender.ToAgentMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoreExecutionServiceImpl implements CoreExecutionService {

    @Resource
    ToAgentMessageSender messageSender;

    @Resource
    ObjectMapper objectMapper;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    CreateStreamReader createStreamReader;

    @Override
    public String SendCommandToAgent(String topicName, String command) {
        return this.SendCommandToAgent(topicName, List.of(command));
    }

    @Override
    public String SendCommandToAgent(String topicName, List<String> commandList) {
        return this.SendCommandToAgent(topicName,"manual-command", commandList);
    }

    @Override
    public String SendCommandToAgent(String topicName, String type, List<String> commandList) {

        OctopusMessage octopusMessage = this.generateOctopusMessage(topicName, type, commandList);

        ExecutionMessage executionMessage = (ExecutionMessage) octopusMessage.getContent();

        String executionMsg;

        try {

            executionMsg = objectMapper.writeValueAsString(executionMessage);
            octopusMessage.setContent(executionMsg);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String resultKey = executionMessage.getResultKey();

        // set up the stream read group
        String group = redisTemplate.opsForStream().createGroup(resultKey, resultKey);
        log.debug("set consumer group for the stream key with => [ {} ]", resultKey);

        // change the redis stream listener container
        createStreamReader.registerStreamReader(resultKey);

        // send the message
        messageSender.send(octopusMessage);

        return resultKey;
    }


    @Override
    public List<String> SendCommandToAgent(List<String> topicNameList, String type, List<String> command) {
        return topicNameList.stream().map(
                topicName -> {
                    return this.SendCommandToAgent(topicName, type, command);
                }
        ).collect(Collectors.toList());
    }

    private OctopusMessage generateOctopusMessage(String topicName, String type, List<String> commandList){

        ExecutionMessage executionMessage = generateExecutionMessage(
                type,
                commandList,
                generateCommandResultKey(topicName)
        );

        return OctopusMessage.builder()
                .type(OctopusMessageType.EXECUTOR)
                .init_time(LocalDateTime.now())
                .content(executionMessage)
                .uuid(topicName)
                .build();
    }

    private ExecutionMessage generateExecutionMessage(String type, List<String> commandList, String resultKey) {

        return ExecutionMessage.builder()
                .type(type)
                .commandList(commandList)
                .resultKey(resultKey)
                .build();

    }

    private String generateCommandResultKey(String topicName) {

        String TimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

        return topicName + "-" + TimeString;
    }

}
