package io.wdd.agent.config.message.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OctopusRabbitMQAdminConfig {

    @Autowired
    ConnectionFactory connectionFactory;

    @Bean
    public RabbitAdmin rabbitAdmin(){

        return new RabbitAdmin(connectionFactory);
    }
}
