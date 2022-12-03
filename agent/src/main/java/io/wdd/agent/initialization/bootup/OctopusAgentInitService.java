package io.wdd.agent.initialization.bootup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import io.wdd.agent.initialization.beans.AgentServerInfo;
import io.wdd.agent.message.ToServerMessage;
import io.wdd.agent.message.handler.OctopusMessageHandler;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.handler.MyRuntimeException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Lazy
@Slf4j
public class OctopusAgentInitService {

    @Resource
    ToServerMessage toServerMessage;

    @Autowired
    OctopusMessageHandler octopusMessageHandler;

    @Resource
    AgentServerInfo agentServerInfo;

    @Value("${octopus.message.init_ttl}")
    String defaultInitRegisterTimeOut;

    @Resource
    ObjectMapper objectMapper;

    public void SendInfoToServer(AgentServerInfo agentServerInfo) {

        toServerMessage.sendInitInfo(agentServerInfo, defaultInitRegisterTimeOut);

    }

    /**
     * listen to the PassThroughTopicName queue from octopus server
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

            // consider the multi-agents register situation
            // judge the machineID begin
            String[] split = octopusMessage.getUuid().split("-");
            if (!agentServerInfo.getMachineId().startsWith(split[split.length - 1])) {
                throw new MyRuntimeException("INIT Message not for this agent !");
            }

            // response chain to handle all kind of type of octopus message
            if (!octopusMessageHandler.handle(octopusMessage)) {
                throw new MyRuntimeException(" Handle Octopus Message Error !");
            }

        } catch (Exception e) {

            // reject the message
            channel.basicNack(deliveryTag, false, true);
            // long deliveryTag, boolean requeue
            // channel.basicReject(deliveryTag,true);

            // 这里只是便于出现死循环时查看
            TimeUnit.SECONDS.sleep(5);

            throw new MyRuntimeException("Octopus Agent Initialization Error, please check !");
        }

        // ack the info
        channel.basicAck(deliveryTag, false);
    }


}
