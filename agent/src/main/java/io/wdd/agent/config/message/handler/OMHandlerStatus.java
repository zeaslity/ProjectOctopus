package io.wdd.agent.config.message.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.agent.status.HealthyReporter;
import io.wdd.common.beans.rabbitmq.OctopusMessage;
import io.wdd.common.beans.rabbitmq.OctopusMessageType;
import io.wdd.common.beans.status.OctopusStatusMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static io.wdd.common.beans.status.OctopusStatusMessage.HEALTHY_STATUS_MESSAGE_TYPE;

@Component
public class OMHandlerStatus extends AbstractOctopusMessageHandler {

    @Resource
    ObjectMapper objectMapper;

    @Resource
    HealthyReporter healthyReporter;

    @Override
    public boolean handle(OctopusMessage octopusMessage) {

        if (!octopusMessage.getType().equals(OctopusMessageType.STATUS)) {
            return next.handle(octopusMessage);
        }

        // handle about the status kind
        try {

            OctopusStatusMessage statusMessage = objectMapper.readValue((String) octopusMessage.getContent(), new TypeReference<OctopusStatusMessage>() {
            });

            String statusType = statusMessage.getType();

            if (statusType.equals(HEALTHY_STATUS_MESSAGE_TYPE)) {
                // healthy check
                healthyReporter.report();
            }


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        return true;
    }
}
