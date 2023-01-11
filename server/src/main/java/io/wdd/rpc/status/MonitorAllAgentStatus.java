package io.wdd.rpc.status;

import io.wdd.common.beans.status.OctopusStatusMessage;
import io.wdd.common.utils.TimeUtils;
import io.wdd.server.beans.vo.ServerInfoVO;
import io.wdd.server.coreService.CoreServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.wdd.common.beans.status.OctopusStatusMessage.ALL_AGENT_STATUS_REDIS_KEY;
import static io.wdd.common.beans.status.OctopusStatusMessage.HEALTHY_STATUS_MESSAGE_TYPE;

/**
 * 获取所有注册的Agent
 * <p>
 * 发送状态检查信息， agent需要update相应的HashMap的值
 * redis --> all-agent-health-map  agent-topic-name : 1
 *  todo 分布式问题，弱网环境，多线程操作同一个hashMap会不会出现冲突
 * <p>
 * 休眠 MAX_WAIT_AGENT_REPORT_STATUS_TIME 秒 等待agent的状态上报
 * <p>
 * 检查相应的 状态HashMap，然后全部置为零
 */
@Service
@Slf4j
public class MonitorAllAgentStatus {

    private static final int MAX_WAIT_AGENT_REPORT_STATUS_TIME = 5;

    private HashMap<String, String> AGENT_HEALTHY_INIT_MAP;
    private List<String> ALL_AGENT_TOPICNAME_LIST;

    @Resource
    RedisTemplate redisTemplate;
    @Resource
    CollectAgentStatus collectAgentStatus;
    @Resource
    CoreServerService coreServerService;

    public void go() {

        try {

            // 1. 获取所有注册的Agent
            // todo need to cache this
            List<ServerInfoVO> allAgentInfo = coreServerService.serverGetAll();
            Assert.notEmpty(allAgentInfo,"not agent registered ! skip the agent healthy status check !");
            ALL_AGENT_TOPICNAME_LIST = allAgentInfo.stream().map(ServerInfoVO::getTopicName).collect(Collectors.toList());

            // 1.1 检查 Agent状态保存数据结构是否正常
            checkOrCreateRedisHealthyKey();

            // 2.发送状态检查信息， agent需要update相应的HashMap的值
            buildAndSendAgentHealthMessage();

            // 3. 休眠 MAX_WAIT_AGENT_REPORT_STATUS_TIME 秒 等待agent的状态上报
            TimeUnit.SECONDS.sleep(MAX_WAIT_AGENT_REPORT_STATUS_TIME);

            // 4.检查相应的 状态HashMap，然后全部置为零
            // todo 存储到某个地方，目前只是打印日志
            updateAllAgentHealthyStatus();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkOrCreateRedisHealthyKey() {

        if (!redisTemplate.hasKey(ALL_AGENT_STATUS_REDIS_KEY)) {
            log.info("ALL_AGENT_STATUS_REDIS_KEY not existed , start to create");

            // build the  redis all agent healthy map struct
            HashMap<String, String> initMap = new HashMap<>(32);
            ALL_AGENT_TOPICNAME_LIST.stream().forEach(
                    agentTopicName -> {
                        initMap.put(agentTopicName, "0");
                    }
            );
            initMap.put("updateTime", TimeUtils.currentTimeString());

            // cache this map struct
            AGENT_HEALTHY_INIT_MAP = initMap;

            redisTemplate.opsForHash().putAll(ALL_AGENT_STATUS_REDIS_KEY, initMap);
        }
    }

    private void buildAndSendAgentHealthMessage() {
        List<OctopusStatusMessage> collect = ALL_AGENT_TOPICNAME_LIST.stream().map(
                agentTopicName -> OctopusStatusMessage.builder()
                        .agentTopicName(agentTopicName)
                        .type(HEALTHY_STATUS_MESSAGE_TYPE)
                        .build()
        ).collect(Collectors.toList());
        collectAgentStatus.collectAgentStatusList(collect);
    }

    private void updateAllAgentHealthyStatus() {
        List statusList = redisTemplate.opsForHash().multiGet(
                ALL_AGENT_STATUS_REDIS_KEY,
                ALL_AGENT_TOPICNAME_LIST);


        // current log to console is ok
        HashMap<String, String> tmp = new HashMap<>(32);
        for (int i = 0; i < ALL_AGENT_TOPICNAME_LIST.size(); i++) {
            tmp.put(
                    ALL_AGENT_TOPICNAME_LIST.get(i),
                    uniformHealthyStatus(String.valueOf(statusList.get(i)))
            );
        }
        String currentTimeString = TimeUtils.currentTimeString();
        log.info("[ AGENT HEALTHY CHECK ] time is {} ,  result are => {}", currentTimeString, tmp);

        // help gc
        tmp = null;

        // update time
        AGENT_HEALTHY_INIT_MAP.put("updateTime", currentTimeString);
        // init the healthy map
        redisTemplate.opsForHash().putAll(ALL_AGENT_STATUS_REDIS_KEY, AGENT_HEALTHY_INIT_MAP);
    }

    private String uniformHealthyStatus(String agentStatus) {
        switch (agentStatus) {
            case "0":
                return "FAILED";
            case "1":
                return "HEALTHY";
            default:
                return "UNKNOWN";
        }
    }


}
