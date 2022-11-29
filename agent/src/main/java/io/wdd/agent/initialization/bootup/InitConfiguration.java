package io.wdd.agent.initialization.bootup;

import io.wdd.agent.initialization.beans.ServerInfo;
import io.wdd.agent.initialization.rabbitmq.InitialRabbitMqConnector;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

@Service
@Lazy
public class InitConfiguration {


    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    InitialRabbitMqConnector initialRabbitMqConnector;

    @Value("${octopus.message.init_ttl}")
    String defaultInitRegisterTimeOut;


    private class InitMessagePostProcessor implements MessagePostProcessor {

        private String initMessageTTL;

        public InitMessagePostProcessor(Long initMessageTTL) {
            this.initMessageTTL = String.valueOf(initMessageTTL);
        }

        public InitMessagePostProcessor(String initMessageTTL) {
            this.initMessageTTL = initMessageTTL;
        }

        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            // set init register expiration time
            MessageProperties messageProperties = message.getMessageProperties();
            messageProperties.setExpiration(initMessageTTL);
            return message;
        }
    }

    public void SendInfoToServer(ServerInfo serverInfo){

        // set init agent register ttl
        InitMessagePostProcessor initMessagePostProcessor = new InitMessagePostProcessor(defaultInitRegisterTimeOut);

        rabbitTemplate.convertAndSend("hello wmm !");

        // send the register server info to EXCHANGE:INIT_EXCHANGE QUEUE: init_to_server
        rabbitTemplate.convertAndSend(initialRabbitMqConnector.INIT_EXCHANGE, initialRabbitMqConnector.INIT_TO_SERVER_KEY, String.valueOf(serverInfo).getBytes(StandardCharsets.UTF_8), initMessagePostProcessor);

    }


    /**
     *  listen to the init queue from octopus server
     *
     * @RabbitListener : 用于标记当前方法是一个RabbitMQ的消息监听方法，可以持续性的自动接收消息
     * @param message
     * 该方法不需要手动调用，Spring会自动运行这个监听方法
     *
     * 注意：如果该监听方法正常结束，那么Spring会自动确认消息
     *      如果出现异常，则Spring不会确认消息，该消息一直存在于消息队列中
     */
    @RabbitHandler
    @RabbitListener(
            bindings  =
                    @QueueBinding(
                            value = @Queue(name = "${octopus.message.init_from_server}" ),
                            exchange = @Exchange(name = "${octopus.message.init_exchange}", type = "direct"),
                            key = {"${octopus.message.init_from_server_key}"}
                    )
            ,
            ackMode = "MANUAL"
    )
    public void ReceiveInitInfoFromServer(String message){

        System.out.println("message = " + message);

    }

}
