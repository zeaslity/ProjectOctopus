package io.wdd.agent.initialization.bootup;


import io.wdd.agent.config.beans.init.AgentServerInfo;
import io.wdd.common.handler.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.Map;
import java.util.Properties;

@Configuration
@Slf4j
public class CollectSystemInfo implements ApplicationContextAware {

    @Resource
    Environment environment;

    private ApplicationContext context;

    @Resource
    OctopusAgentInitService octopusAgentInitService;

    public AgentServerInfo agentServerInfo;

    @Bean
    @Lazy
    public void initialReadingEnvironment(){

        // https://zhuanlan.zhihu.com/p/449416472
        // https://cloud.tencent.com/developer/article/1919814
        // https://blog.51cto.com/binghe001/5244823


        try{

            Properties props =System.getProperties();

            System.out.println("props = " + props);
            System.out.println();

            Map<String, String> getenv = System.getenv();

            System.out.println("getenv = " + getenv);

            System.out.println();

            System.out.println("environment = " + environment);

            InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

            System.out.println("loopbackAddress = " + loopbackAddress);

            InetAddress ip = InetAddress.getLocalHost();
            String localName = ip.getHostName();

            System.out.println("ip = " + ip);

            String osName = System.getProperty("os.name");
            String userName = System.getProperty("user.name");
            String osVersion = System.getProperty("os.version");
            String osArch = System.getProperty("os.arch");

            System.out.println("当前用户：" + userName);
            System.out.println("用户的主目录："+props.getProperty("user.home"));
            System.out.println("用户的当前工作目录："+props.getProperty("user.dir"));
            System.out.println("主机名称：" + localName);
            System.out.println("主机系统：" + osName);
            System.out.println("系统版本：" + osVersion);
            System.out.println("系统架构：" + osArch);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @PostConstruct
    private void getInjectServerInfo(){

        log.info("Octopus Agent -- Starting getInjectServerInfo");

        agentServerInfo = (AgentServerInfo) context.getBean("agentServerInfo");

        if (ObjectUtils.isEmpty(agentServerInfo)) {
            throw new MyRuntimeException(" Collect server info error !");
        }

        //log.info("host server info has been collected == {}", agentServerInfo);

        // start to send message to Octopus Server
        octopusAgentInitService.SendInfoToServer(agentServerInfo);

        //log.info("PassThroughTopicName server info has been send to octopus server !");

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
