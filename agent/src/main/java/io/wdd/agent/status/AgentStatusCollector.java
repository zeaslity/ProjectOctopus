package io.wdd.agent.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.agent.config.beans.init.AgentServerInfo;
import io.wdd.agent.config.utils.TimeUtils;
import io.wdd.agent.status.hardware.CpuInfo;
import io.wdd.agent.status.hardware.MemoryInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AgentStatusCollector {

    private static final SystemInfo systemInfo;
    /**
     * 硬件信息
     */
    private static final HardwareAbstractionLayer hardware;
    /**
     * 系统信息
     */
    private static final OperatingSystem os;
    private static final List<AgentStatus> AgentStatusCache = Collections.singletonList(new AgentStatus());

    static {
        systemInfo = new SystemInfo();
        hardware = systemInfo.getHardware();
        os = systemInfo.getOperatingSystem();
    }

    @Resource
    RedisTemplate redisTemplate;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    AgentServerInfo agentServerInfo;

    public AgentStatus collect() {

        AgentStatus agentStatus = AgentStatusCache.get(0);

        /* base */
        agentStatus.setAgentName(agentServerInfo.getServerName());
        agentStatus.setAgentTopicName(agentServerInfo.getAgentTopicName());

        /* CPU */
        agentStatus.setCpuInfo(new CpuInfo(hardware.getProcessor(), 1000));

        /* Memory */
        agentStatus.setMemoryInfo(new MemoryInfo().build(hardware.getMemory()));

        /* Storage */
        agentStatus.setDiskStoreInfo(hardware.getDiskStores());

        /* Network */
        agentStatus.setNetworkInfo(hardware.getNetworkIFs(false));

        /* operating system info */
        agentStatus.setOsInfo(os);

        /* Time */
        agentStatus.setTime(TimeUtils.currentTimeString());

        return agentStatus;
    }


    // agent boot up 60s then start to report its status
    // at the fix rate of 15s
    @Scheduled(initialDelay = 60000, fixedRate = 5000)
    public void sendAgentStatusToRedis() {

        try {

            String statusStreamKey = agentServerInfo.getServerName() + "-status";

            Map<String, String> map = Map.of(TimeUtils.currentTimeString(), objectMapper.writeValueAsString(collect()));

            StringRecord stringRecord = StreamRecords.string(map).withStreamKey(statusStreamKey);

            log.debug("Agent Status is ==> {}",map);
            redisTemplate.opsForStream().add(stringRecord);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}
