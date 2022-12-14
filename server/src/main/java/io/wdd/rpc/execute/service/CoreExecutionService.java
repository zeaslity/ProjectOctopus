package io.wdd.rpc.execute.service;

import org.springframework.stereotype.Service;

import java.util.List;


public interface CoreExecutionService {


    void SendCommandToAgent(String topicName, String command);

    void SendCommandToAgent(String topicName, List<String> commandList);


    void SendCommandToAgent(String topicName, String type, List<String> command);

    void SendCommandToAgent(List<String> topicNameList, String type, String command);

    void SendCommandToAgent(List<String> topicNameList, String type, List<String> command);


}
