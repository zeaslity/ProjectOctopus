package io.wdd.rpc.status;

import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import io.wdd.common.beans.status.OctopusStatusMessage;
import io.wdd.common.utils.TimeUtils;
import io.wdd.rpc.message.sender.ToAgentMessageSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 1. 定时任务
 * 2. 向RabbitMQ中发送消息，STATUS类型的消息
 * 3. 然后开始监听相应的Result  StreamKey
 */
@Service
public class CollectAgentStatus {

    @Resource
    ToAgentMessageSender toAgentMessageSender;


    public void collectAgentStatus(OctopusStatusMessage statusMessage) {

        this.collectAgentStatusList(List.of(statusMessage));
    }


    public void collectAgentStatusList(List<OctopusStatusMessage> statusMessageList) {

        // build all the OctopusMessage
        List<OctopusMessage> octopusMessageList = statusMessageList.stream().map(
                statusMessage -> {
                    OctopusMessage octopusMessage = buildOctopusMessageStatus(statusMessage);
                    return octopusMessage;
                }
        ).collect(Collectors.toList());

        // batch send all messages to RabbitMQ
        toAgentMessageSender.send(octopusMessageList);

        // todo how to get result ?

    }

    private OctopusMessage buildOctopusMessageStatus(OctopusStatusMessage octopusStatusMessage) {
        return OctopusMessage.builder()
                .uuid(octopusStatusMessage.getAgentTopicName())
                .type(OctopusMessageType.STATUS)
                .init_time(TimeUtils.currentTime())
                .content(octopusStatusMessage)
                .build();
    }


}
