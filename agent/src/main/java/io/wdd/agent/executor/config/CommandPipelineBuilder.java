package io.wdd.agent.executor.config;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * https://stackoverflow.com/questions/3776195/using-java-processbuilder-to-execute-a-piped-command
 * https://www.baeldung.com/java-lang-processbuilder-api
 * https://docs.oracle.com/javase/9/docs/api/java/lang/ProcessBuilder.html#startPipeline-java.util.List-
 */
@Slf4j
public class CommandPipelineBuilder {


    public static List<String> runGetResult(ArrayList<ArrayList<String>> commandList){

        try {
            List<Process> processList = build(commandList);
            int lastCommandIndex = commandList.size() - 1;

            List<String> resultList = new BufferedReader(new InputStreamReader(processList.get(lastCommandIndex).getInputStream())).lines().collect(Collectors.toList());

            log.debug("command => [ {} ] , execute result is [ {} ]", commandList, resultList);

            // error stream can't be got
            // maybe this is the design patten of ProcessBuilder
            /*List<String> error = new BufferedReader(new InputStreamReader(processList.get(lastCommandIndex).getErrorStream())).lines().collect(Collectors.toList());
            log.debug("command => {} error is {}", commandList, error);*/

            return resultList;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<Process> build(ArrayList<ArrayList<String>> commandList) throws IOException {

        int length = commandList.size();

        List<Process> result = null;

        switch (length) {
            case 2:
                result = build(commandList.get(0), commandList.get(1));
                break;
            case 3:
                result = build(commandList.get(0), commandList.get(1), commandList.get(2));
                break;
            case 4:
                result = build(commandList.get(0), commandList.get(1), commandList.get(2), commandList.get(3));
                break;
            default:
                result = build(commandList.get(0));
                break;
        }

        return result;
    }

    private static List<Process> build(List<String> commandList1) throws IOException {

        return ProcessBuilder.startPipeline(List.of(
                new ProcessBuilder(commandList1)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
        ));
    }

    private static List<Process> build(List<String> commandList1, List<String> commandList2) throws IOException {

        return ProcessBuilder.startPipeline(List.of(
                new ProcessBuilder(commandList1)
                        .inheritIO()
                        .redirectOutput(ProcessBuilder.Redirect.PIPE),
                new ProcessBuilder(commandList2)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
        ));
    }


    private static List<Process> build(List<String> commandList1, List<String> commandList2, List<String> commandList3) throws IOException {

        return ProcessBuilder.startPipeline(List.of(
                new ProcessBuilder(commandList1)
                        .inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE),
                new ProcessBuilder(commandList2)
                        .inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE),
                new ProcessBuilder(commandList3)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
        ));

    }

    private static List<Process> build(List<String> commandList1, List<String> commandList2, List<String> commandList3, List<String> commandList4) throws IOException {

        return ProcessBuilder.startPipeline(List.of(
                new ProcessBuilder(commandList1)
                        .inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE),
                new ProcessBuilder(commandList2)
                        .inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE),
                new ProcessBuilder(commandList3)
                        .inheritIO().redirectOutput(ProcessBuilder.Redirect.PIPE),
                new ProcessBuilder(commandList4)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
        ));
    }

}
