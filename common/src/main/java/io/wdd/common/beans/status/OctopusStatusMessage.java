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

    /**
    * which kind of status should be return
     * short => short time message
     * all => all agent status message
     * healthy => check for healthy
    * */
    String type;

    String agentTopicName;


}
