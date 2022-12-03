package io.wdd.rpc.init;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  generate the OCTOPUS_EXCHANGE at the beginning
 */
@Configuration
public class OctopusExchangeConfig {

    @Value("${octopus.message.octopus_exchange}")
    public String OCTOPUS_EXCHANGE;

    @Value("${octopus.message.octopus_to_server}")
    public String OCTOPUS_TO_SERVER;


    @Bean
    public TopicExchange octopusExchange(){
        return new TopicExchange(OCTOPUS_EXCHANGE,true,false);
    }

    @Bean
    public Queue octopusAgentToServerQueue(){
        return new Queue(OCTOPUS_TO_SERVER);
    }

    @Bean
    public Binding bindingToServerTopicQueue(TopicExchange octopusExchange, Queue octopusAgentToServerQueue){
        return BindingBuilder
                .bind(octopusAgentToServerQueue)
                .to(octopusExchange)
                .with(OCTOPUS_TO_SERVER);
    }

}
