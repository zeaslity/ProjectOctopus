package io.wdd.agent.config.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.common.handler.MyRuntimeException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;


//@Configuration
@Deprecated
public class BeanFactoryUtils implements ApplicationContextAware {


    private ApplicationContext applicationContext;


    @Bean
    public ObjectMapper getMapper() {

        ObjectMapper objectMapper = (ObjectMapper) applicationContext.getBean("jacksonObjectMapper");
        if (ObjectUtils.isEmpty(objectMapper)) {
            throw new MyRuntimeException(" Collect server info error !");
        }

        return objectMapper;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
