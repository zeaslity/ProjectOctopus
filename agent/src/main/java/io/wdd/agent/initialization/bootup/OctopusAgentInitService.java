package io.wdd.agent.initialization.bootup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import io.wdd.agent.initialization.beans.AgentServerInfo;
import io.wdd.agent.initialization.rabbitmq.InitialRabbitMqConnector;
import io.wdd.agent.message.handler.OctopusMessageHandler;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.handler.MyRuntimeException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Lazy
@Slf4j
public class OctopusAgentInitService {


    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    InitialRabbitMqConnector initialRabbitMqConnector;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OctopusMessageHandler octopusMessageHandler;


    @Value("${octopus.message.init_ttl}")
    String defaultInitRegisterTimeOut;

    @SneakyThrows
    public void SendInfoToServer(AgentServerInfo agentServerInfo) {

        // set init agent register ttl
        InitMessagePostProcessor initMessagePostProcessor = new InitMessagePostProcessor(defaultInitRegisterTimeOut);

        log.info("send INIT AgentServerInfo to Server = {}", agentServerInfo);

        // send the register server info to EXCHANGE:INIT_EXCHANGE QUEUE: init_to_server
        rabbitTemplate.convertAndSend(initialRabbitMqConnector.INIT_EXCHANGE, initialRabbitMqConnector.INIT_TO_SERVER_KEY, objectMapper.writeValueAsString(agentServerInfo), initMessagePostProcessor);

    }

    /**
     * listen to the init queue from octopus server
     *
     * @param message 该方法不需要手动调用，Spring会自动运行这个监听方法
     *                <p>
     *                注意：如果该监听方法正常结束，那么Spring会自动确认消息
     *                如果出现异常，则Spring不会确认消息，该消息一直存在于消息队列中
     * @RabbitListener : 用于标记当前方法是一个RabbitMQ的消息监听方法，可以持续性的自动接收消息
     */
    @SneakyThrows
    @RabbitHandler
    @RabbitListener(
            bindings =
            @QueueBinding(
                    value = @Queue(name = "${octopus.message.init_from_server}"),
                    exchange = @Exchange(name = "${octopus.message.init_exchange}", type = "direct"),
                    key = {"${octopus.message.init_from_server_key}"}
            )
            ,
            ackMode = "MANUAL"
    )
    public void ReceiveInitInfoFromServer(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {


        try {

            OctopusMessage octopusMessage = objectMapper.readValue(message.getBody(), OctopusMessage.class);

            // response chain to handle all kind of type of octopus message
            if (!octopusMessageHandler.handle(octopusMessage)) {
                throw new MyRuntimeException(" Handle Octopus Message Error !");
            }

        } catch (Exception e) {

            // reject the message
            channel.basicNack(deliveryTag, false, true);
            // long deliveryTag, boolean requeue
            // channel.basicReject(deliveryTag,true);

            Thread.sleep(1000);     // 这里只是便于出现死循环时查看


            throw new MyRuntimeException("Octopus Agent Initialization Error, please check !");
        }

        // ack the info
        channel.basicAck(deliveryTag, false);
    }

    private class InitMessagePostProcessor implements MessagePostProcessor {

        private final String initMessageTTL;

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

}
