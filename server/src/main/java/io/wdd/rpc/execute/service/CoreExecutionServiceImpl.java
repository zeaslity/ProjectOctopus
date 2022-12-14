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
import java.util.stream.Collectors;

@Service
public class CoreExecutionServiceImpl implements CoreExecutionService {

    @Resource
    ToAgentMessageSender messageSender;


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

        messageSender.send(octopusMessage);

        ExecutionMessage content = (ExecutionMessage) octopusMessage.getContent();

        return content.getResultKey();
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

        return topicName + "-" + TimeString;
    }

}
