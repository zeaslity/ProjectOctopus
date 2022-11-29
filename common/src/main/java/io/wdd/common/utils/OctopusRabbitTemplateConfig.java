package io.wdd.common.utils;


import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;

@Configuration
public class OctopusRabbitTemplateConfig {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter octopusMessageConverter() {
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        return new Jackson2JsonMessageConverter(jsonMapper,
                "io.wdd.common.beans.rabbitmq");
    }

    @Bean
    public AmqpTemplate OctopusRabbitTemplate(ConnectionFactory connectionFactory) {

        rabbitTemplate.setMessageConverter(octopusMessageConverter());
        return rabbitTemplate;
    }
}
