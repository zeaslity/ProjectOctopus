package io.wdd.agent.initial.bootup;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.Properties;

@Configuration
public class collectSystemInfo {




    @Resource
    Environment environment;


    @Bean
    public void initialReadingEnvironment(){

        // https://zhuanlan.zhihu.com/p/449416472
        // https://cloud.tencent.com/developer/article/1919814
        // https://blog.51cto.com/binghe001/5244823


        try{

            Properties props =System.getProperties();

            InetAddress ip = InetAddress.getLocalHost();
            String localName = ip.getHostName();
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

}
