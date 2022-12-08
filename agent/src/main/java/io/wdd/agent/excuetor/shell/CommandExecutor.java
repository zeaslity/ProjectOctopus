package io.wdd.agent.excuetor.shell;

import com.google.common.io.ByteStreams;
import io.wdd.agent.excuetor.redis.StreamSender;
import io.wdd.agent.excuetor.thread.DaemonLogThread;
import io.wdd.agent.excuetor.thread.LogToStreamSender;
import io.wdd.agent.excuetor.thread.LogToSysOut;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@Configuration
public class CommandExecutor {

    @Resource
    StreamSender streamSender;

    public void execute(String streamKey, String... command) throws IOException, InterruptedException, ExecutionException {

        ProcessBuilder processBuilder = new ProcessBuilder(command);

//        processBuilder.redirectErrorStream(true);
//        processBuilder.inheritIO();
        processBuilder.directory(new File(System.getProperty("user.home")));
        Process process = processBuilder.start();

        LogToStreamSender toStreamSender = new LogToStreamSender(streamKey, process.getInputStream(), streamSender::send);

//        LogToSysOut(process.getInputStream(), System.out::println);

        // a command shell don't understand how long it actually take
        int processResult = process.waitFor();
        System.out.println("processResult = " + processResult);

        Future<?> future = DaemonLogThread.start(toStreamSender);

        System.out.println("future.get() = " + future.get());
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
