package io.wdd.agent;

import io.wdd.agent.executor.shell.CommandExecutor;

import javax.annotation.Resource;

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
