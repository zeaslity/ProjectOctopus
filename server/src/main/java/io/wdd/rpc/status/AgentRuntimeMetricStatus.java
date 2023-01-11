package io.wdd.rpc.status;


import io.wdd.common.beans.status.OctopusStatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static io.wdd.common.beans.status.OctopusStatusMessage.METRIC_STATUS_MESSAGE_TYPE;

/**
 * 收集OctopusAgent的运行Metric信息
 * <p>
 * CPU Memory AppStatus易变信息
 */
@Service
@Slf4j
public class AgentRuntimeMetricStatus {

    public static List<String> ALL_HEALTHY_AGENT_TOPIC_NAMES;

    @Resource
    CollectAgentStatus collectAgentStatus;

    public void collect(int metricRepeatCount, int metricRepeatPinch) {

        // 检查基础信息
        if (CollectionUtils.isEmpty(ALL_HEALTHY_AGENT_TOPIC_NAMES)) {
            log.error("Metric Status Collect Failed ! no ALL_HEALTHY_AGENT_TOPIC_NAMES");
        }
        // 构建 OctopusMessage
            // 只发送一次消息，让Agent循环定时执行任务
        buildMetricStatusMessageAndSend(metricRepeatCount, metricRepeatPinch);

        //
    }

    private void buildMetricStatusMessageAndSend(int metricRepeatCount, int metricRepeatPinch) {

        List<OctopusStatusMessage> collect = ALL_HEALTHY_AGENT_TOPIC_NAMES.stream()
                .map(
                        agentTopicName -> {
                            return OctopusStatusMessage.builder()
                                    .type(METRIC_STATUS_MESSAGE_TYPE)
                                    .metricRepeatCount(metricRepeatCount)
                                    .metricRepeatCount(metricRepeatCount)
                                    .agentTopicName(agentTopicName)
                                    .build();
                        }
                ).collect(Collectors.toList());

        // send to the next level
        collectAgentStatus.statusMessageToAgent(collect);

    }

}
