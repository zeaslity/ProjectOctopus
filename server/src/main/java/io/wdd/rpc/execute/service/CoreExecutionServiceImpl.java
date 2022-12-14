package io.wdd.rpc.execute.service;

import io.wdd.common.beans.executor.ExecutionMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import io.wdd.rpc.message.sender.ToAgentMessageSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CoreExecutionServiceImpl implements CoreExecutionService {

    @Resource
    ToAgentMessageSender messageSender;


    @Override
    public void SendCommandToAgent(String topicName, String command) {
        this.SendCommandToAgent(topicName, List.of(command));
    }

    @Override
    public void SendCommandToAgent(String topicName, List<String> commandList) {
        this.SendCommandToAgent(topicName,"manual-command", commandList);

    }

    @Override
    public void SendCommandToAgent(String topicName, String type, List<String> commandList) {

    }

    @Override
    public void SendCommandToAgent(List<String> topicNameList, String type, String command) {

    }

    @Override
    public void SendCommandToAgent(List<String> topicNameList, String type, List<String> command) {

    }

    private OctopusMessage generateOctopusMessage(String topicName, String type, List<String> commandList){


        return OctopusMessage.builder()
                .type(OctopusMessageType.EXECUTOR)
                .init_time(LocalDateTime.now())
                .content(generateExecutionMessage(
                        type,
                        commandList,
                        generateCommandResultKey(topicName)
                ))
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

        String TimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        return topicName + TimeString;
    }

}
