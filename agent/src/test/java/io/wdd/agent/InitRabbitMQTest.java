package io.wdd.agent;

import io.wdd.agent.excuetor.shell.CommandExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

//@SpringBootTest
public class InitRabbitMQTest {

    @Resource
    CommandExecutor commandExecutor;


//    @Test
    void testInitSendInfo() {

        String homeDirectory = System.getProperty("user.home");

        String format = String.format("C:\\program files\\powershell\\7\\pwsh.exe /c dir %s | findstr \"Desktop\"", homeDirectory);

        commandExecutor.execute("sasda",
                "C:\\program files\\powershell\\7\\pwsh.exe",
                "pwd");


    }
}
