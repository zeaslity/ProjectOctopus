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

    /**
    * which kind of status should be return
     * short => short time message
     * all => all agent status message
     * healthy => check for healthy
    * */
    String type;

    String agentTopicName;


}
