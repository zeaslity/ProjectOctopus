package io.wdd.rpc.execute.service;

import java.util.List;


public interface CoreExecutionService {


    String SendCommandToAgent(String topicName, String command);

    String SendCommandToAgent(String topicName, List<String> commandList);


    String SendCommandToAgent(String topicName, String type, List<String> command);


    List<String> SendCommandToAgent(List<String> topicNameList, String type, List<String> command);


}
