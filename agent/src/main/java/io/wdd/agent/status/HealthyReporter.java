package io.wdd.agent.status;

import io.wdd.agent.config.beans.init.AgentServerInfo;
import io.wdd.common.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static io.wdd.common.beans.status.OctopusStatusMessage.ALL_AGENT_STATUS_REDIS_KEY;

/**
 * 1. modify the redis key => ALL_AGENT_STATUS_REDIS_KEY
 * 2. the hashmap struct  key => agentTopicName
 * 3. modify the key to "1"
 */
@Service
@Slf4j
public class HealthyReporter {

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    AgentServerInfo agentServerInfo;

    public void report(){

        redisTemplate.opsForHash().put(
                ALL_AGENT_STATUS_REDIS_KEY,
                agentServerInfo.getAgentTopicName(),
                "1"
        );

        log.debug("Agent Healthy Check Complete ! Time is => {}", TimeUtils.currentTimeString());
    }


}
