package io.wdd.agent.initial.bootup.reference;



import io.wdd.agent.initial.beans.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.Map;
import java.util.Properties;

@Configuration
@Slf4j
public class collectSystemInfo implements ApplicationContextAware {

    @Resource
    Environment environment;

    private ApplicationContext context;

    @Bean
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

        log.info("getInjectServerInfo");

        ServerInfo serverInfo = (ServerInfo) context.getBean("serverInfo");


        System.out.println("serverInfo = " + serverInfo);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
