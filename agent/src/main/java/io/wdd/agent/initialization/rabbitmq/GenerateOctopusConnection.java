package io.wdd.agent.initialization.rabbitmq;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.agent.message.handler.OctopusMessageHandler;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.handler.MyRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Lazy
@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateOctopusConnection {

    private final List<MessageListenerContainer> messageListenerContainerList = new ArrayList<>();
    private final SimpleRabbitListenerContainerFactory containerFactory;
    @Resource
    RabbitAdmin rabbitAdmin;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    OctopusMessageHandler octopusMessageHandler;

    public void ManualGenerate(OctopusMessage octopusMessage) {

        // generate the ne topic queue for unique agent
        String agentTopicName = octopusMessage.getUuid();

        // reboot judgyment of existing exchange
        QueueInformation queueInfo = rabbitAdmin.getQueueInfo(agentTopicName);

        if (ObjectUtils.isNotEmpty(queueInfo) && queueInfo.getConsumerCount() > 0 ) {
            log.info("Octopus Agent Specific Topic Queue Already Existed ! == {}", agentTopicName);
            return;
        }

        Queue queue = new Queue(agentTopicName, true, false, false);
        Binding binding = new Binding(
                agentTopicName,
                Binding.DestinationType.QUEUE,
                octopusMessage.getContent().toString(),
                agentTopicName + "*",
                null
        );

        // Exchange are created by Octopus Server at server BootUP
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);


        // create the listener
        SimpleMessageListenerContainer listenerContainer = containerFactory.createListenerContainer();
        listenerContainer.addQueues(queue);
        listenerContainer.setMessageListener(this::AgentListenToSpecificTopicOctopusMessage);
        listenerContainer.start();


        log.info("Specific Octopus Topic Queue Generate Successfully !");
        messageListenerContainerList.add(listenerContainer);
    }


    /**
     * Maunally Generated Octopus Message Listener
     *
     * @param message Rabbitmq Message
     */
    public void AgentListenToSpecificTopicOctopusMessage(Message message) {

        OctopusMessage octopusMessage;


        try {
            octopusMessage = objectMapper.readValue(message.getBody(), OctopusMessage.class);

            if (!octopusMessageHandler.handle(octopusMessage)) {
                throw new MyRuntimeException("Octopus Message Handle Err");
            }

        } catch (IOException e) {

            throw new MyRuntimeException("Octopus Message Wrong !");
        }
    }

    @PreDestroy
    public void destroy() {
        messageListenerContainerList.forEach(Lifecycle::stop);
        log.info("- stop all message listeners...");
    }


}
