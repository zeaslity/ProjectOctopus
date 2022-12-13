package io.wdd.agent.executor;

import com.google.common.io.ByteStreams;
import io.wdd.agent.executor.redis.StreamSender;
import io.wdd.agent.executor.thread.DaemonLogThread;
import io.wdd.agent.executor.thread.LogToStreamSender;
import io.wdd.common.beans.executor.ExecutionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.List;


@Configuration
@Slf4j
public class CommandExecutor {

    @Resource
    StreamSender streamSender;


    /**
     * handle command from octopus server
     *
     * @param executionMessage get from EXECUTOR_HANDLER
     */
    public void execute(ExecutionMessage executionMessage) {

    }


    public int execute(String streamKey, List<String> command) {

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        return this.processExecute(streamKey, processBuilder);
    }

    public int execute(String streamKey, String... command) {

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        return this.processExecute(streamKey, processBuilder);

    }


    public int processExecute(String streamKey, ProcessBuilder processBuilder) {

        processBuilder.redirectErrorStream(true);
//        processBuilder.inheritIO();
//        processBuilder.directory(new File(System.getProperty("user.home")));

        int processResult = 233;

        try {

            Process process = processBuilder.start();



            LogToStreamSender toStreamSender = new LogToStreamSender(streamKey, process.getInputStream(), streamSender::send);
            DaemonLogThread.start(toStreamSender);

            log.warn("---------------------------------------------");
            new BufferedReader(new InputStreamReader(process.getInputStream())).lines()
                    .map(
                            String::valueOf
                    ).forEach(System.out::println);
            log.warn("---------------------------------------------");

            // a command shell don't understand how long it actually take
            processResult = process.waitFor();

            log.info("current shell command {} result is {}", processBuilder.command(), processResult);


        } catch (IOException | InterruptedException e) {
            log.error("Shell command error ! {} + {}", e.getCause(), e.getMessage());
        }

        return processResult;
    }

    private ByteBuffer cvToByteBuffer(InputStream inputStream) throws IOException {

        byte[] toByteArray = ByteStreams.toByteArray(inputStream);

        ByteBuffer bufferByte = ByteBuffer.wrap(toByteArray);

        return bufferByte;
    }

    private String cvToString(InputStream inputStream) throws IOException {

        String s = String.valueOf(ByteStreams.toByteArray(inputStream));

        System.out.println("s = " + s);

        return s;

    }


}