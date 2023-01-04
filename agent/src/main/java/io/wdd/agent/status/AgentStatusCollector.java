package io.wdd.agent.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.agent.config.beans.init.AgentServerInfo;
import io.wdd.agent.config.utils.TimeUtils;
import io.wdd.agent.status.hardware.CpuInfo;
import io.wdd.agent.status.hardware.MemoryInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AgentStatusCollector {

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    ObjectMapper objectMapper;

    @Resource
    AgentServerInfo agentServerInfo;


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


    public void sendAgentStatusToRedis() {

        try {

            log.info("time is [{}] , and agent status are [{}]", LocalDateTime.now(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(collect()));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}
