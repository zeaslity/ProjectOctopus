package io.wdd.common.beans.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class OctopusStatusMessage {

    // below two will be used by both server and agent
    public static final String ALL_AGENT_STATUS_REDIS_KEY = "ALL_AGENT_STATUS";
    public static final String HEALTHY_STATUS_MESSAGE_TYPE = "ping";
    public static final String ALL_STATUS_MESSAGE_TYPE = "all";
    public static final String METRIC_STATUS_MESSAGE_TYPE = "metric";
    public static final String APP_STATUS_MESSAGE_TYPE = "app";

    /**
    * which kind of status should be return
     * metric => short time message
     * all => all agent status message
     * healthy => check for healthy
    * */
    String type;

    String agentTopicName;


}
