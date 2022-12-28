package io.wdd.agent.executor;

import com.google.common.io.ByteStreams;
import io.wdd.agent.executor.redis.StreamSender;
import io.wdd.agent.executor.thread.LogToArrayListCache;
import io.wdd.common.beans.executor.ExecutionMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;


@Configuration
@Slf4j
public class CommandExecutor {

    @Resource
    StreamSender streamSender;

    @Resource
    LogToArrayListCache logToArrayListCache;

    int processMaxWaitSeconds = 60;

    ExecutorService DaemonCommandProcess = Executors.newFixedThreadPool(1);

    /**
     * handle command from octopus server
     *
     * @param executionMessage get from EXECUTOR_HANDLER
     */
    public void execute(ExecutionMessage executionMessage) {


        this.execute(executionMessage.getResultKey(), executionMessage.getCommandList());


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
        processBuilder.directory(new File(System.getProperty("user.home")));
        int processResult = 233;

        try {

            Process process = processBuilder.start();
            // start a backend thread to daemon the process
            // wait for processMaxWaitSeconds and kill the process if it's still alived
            DaemonCommandProcess.submit(
                    StopStuckCommandProcess(
                            process,
                            processMaxWaitSeconds
                    ));

            // cache log lines
            logToArrayListCache.cacheLog(streamKey, process.getInputStream());

            // start to send the result log
            streamSender.startToWaitLog(streamKey);

            // get the command result
            processResult = process.waitFor();

            // end send logs
            streamSender.endWaitLog(streamKey);

            log.debug("current shell command {} result is {}", processBuilder.command(), processResult);


        } catch (IOException | InterruptedException e) {
            log.error("Shell command error ! {} + {}", e.getCause(), e.getMessage());
        }

        return processResult;
    }

    private Runnable StopStuckCommandProcess(Process process, int processMaxWaitSeconds)  {
        return () -> {
            try {


                log.debug("daemon thread start to wait for {} s for the result", processMaxWaitSeconds);

                TimeUnit.SECONDS.sleep(processMaxWaitSeconds);

                if (process.isAlive()) {

                    log.warn("Command [ {} ] stuck for {} s, destroy the command process !", process.info().commandLine().get(), processMaxWaitSeconds);

                    // shutdown the process
                    process.destroyForcibly();
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
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


    @SneakyThrows
    public void clearCommandCache(String streamKey) {

        // wait
        TimeUnit.SECONDS.sleep(1);

        // clear the log Cache Thread scope
        logToArrayListCache.getExecutionCmdCachedLogArrayList(streamKey).clear();

        // clear the stream sender
        streamSender.clearLocalCache(streamKey);

    }
}
