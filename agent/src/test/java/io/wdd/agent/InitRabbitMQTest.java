package io.wdd.agent;

import io.wdd.agent.initialization.bootup.OctopusAgentInitService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class InitRabbitMQTest {

    @Resource
    OctopusAgentInitService octopusAgentInitService;

    @Test
    void testInitSendInfo(){

    }
}
