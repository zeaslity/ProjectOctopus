package io.wdd.agent.initialization.message;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * agent send server PassThroughTopicName info to octopus server
 * <p>
 * agent boot up should send message to server using this queue
 */
@Configuration
public class InitRabbitMQConnector {

    //
    public static String SPECIFIC_AGENT_TOPIC_NAME;
    @Value("${octopus.message.init_exchange}")
    public String INIT_EXCHANGE;
    @Value("${octopus.message.init_from_server}")
    public String INIT_FROM_SERVER;
    @Value("${octopus.message.init_to_server}")
    public String INIT_TO_SERVER;
    @Value("${octopus.message.init_from_server_key}")
    public String INIT_FROM_SERVER_KEY;
    @Value("${octopus.message.init_to_server_key}")
    public String INIT_TO_SERVER_KEY;
    @Value("${octopus.message.octopus_exchange}")
    public String OCTOPUS_EXCHANGE;
    @Value("${octopus.message.octopus_to_server}")
    public String OCTOPUS_TO_SERVER;

    @Bean
    public DirectExchange initDirectExchange() {
        return new DirectExchange(INIT_EXCHANGE);
    }

    @Bean
    public Queue initDirectQueue() {
        return new Queue(INIT_TO_SERVER);
    }


    /**
     * 配置一个队列和交换机的绑定
     *
     * @param initDirectQueue    : 需要绑定的队列对象，参数名必须和某个@Bean的方法名完全相同，这样就会进行自动注入，对应 .bind()
     * @param initDirectExchange : 需要绑定的交换机对象，参数名必须和某个@Bean的方法名完全相同，这样就会进行自动注入，对应 .to()
     *                           .with() 方法对应的RoutingKey
     * @return
     */
    @Bean
    public Binding initBinding(DirectExchange initDirectExchange, Queue initDirectQueue) {
        return BindingBuilder.bind(initDirectQueue).to(initDirectExchange).with(INIT_TO_SERVER_KEY);
    }


}
