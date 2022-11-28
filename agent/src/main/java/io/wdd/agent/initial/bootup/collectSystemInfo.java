package io.wdd.agent.initial.bootup;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class collectSystemInfo {




    @Resource
    Environment environment;


    @Bean
    public void initialReadingEnvironment(){






    }

}
