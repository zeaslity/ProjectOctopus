package io.wdd.agent.executor.config;


import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FunctionReader {


    public List<List<String>> ReadFileToCommandList(String functionFilePath) {

        // https://www.digitalocean.com/community/tutorials/java-read-file-line-by-line

        List<List<String>> result = null;

        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(functionFilePath));
            result = doReadContent(result, bufferedReader);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;

    }


    public List<List<String>> ReadStringToCommandList(String functionContent) {

        List<List<String>> result = null;

        try {

            BufferedReader bufferedReader = new BufferedReader(new StringReader(functionContent));
            result = doReadContent(result, bufferedReader);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;

    }

    private List<List<String>> doReadContent(List<List<String>> result, BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();

        if (line != null) {
            result = new ArrayList<>(64);
        }

        while (line != null) {
            if (!StringUtils.isEmpty(line)) {
                result.add(this.SplitLineToCommandList(line));
            }
            line = bufferedReader.readLine();
        }
        return result;
    }

    public List<String> SplitLineToCommandList(String commandLine) {

        return Arrays.stream(commandLine.split(" ")).collect(Collectors.toList());
    }


}
