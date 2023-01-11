package io.wdd.agent.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wdd.agent.config.beans.init.AgentServerInfo;
import io.wdd.agent.executor.AppStatusExecutor;
import io.wdd.common.beans.status.*;
import io.wdd.common.utils.TimeUtils;
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
import java.util.*;

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
    private static final long ReportInitDelay = 60000;
    private static final long ReportFixedRate = 15000;

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

    @Resource
    AppStatusExecutor appStatusExecutor;

    public AgentStatus collect() {

        AgentStatus agentStatus = AgentStatusCache.get(0);

        /* base */
        agentStatus.setAgentName(agentServerInfo.getServerName());
        agentStatus.setAgentTopicName(agentServerInfo.getAgentTopicName());

        /* CPU */
        agentStatus.setCpuInfo(new CpuInfo(hardware.getProcessor(), 1000));

        /* Memory */
        agentStatus.setMemoryInfo(MemoryInfo.build(hardware.getMemory()));

        /* Storage */
        agentStatus.setDiskStoreInfo(DiskInfo.mapFromDiskStore(hardware.getDiskStores()));

        /* Network */
        agentStatus.setNetworkInfo(NetworkInfo.mapFromNetworkIFS(hardware.getNetworkIFs(false)));

        /* operating system info */
        agentStatus.setOsInfo(AgentSystemInfo.mapFromOHSISystem(os));

        /* Time */
        agentStatus.setTime(TimeUtils.currentTimeString());

        /* App Status */
        agentStatus.setAppStatus(
                parseAppStatus(appStatusExecutor.checkAppStatus(true))
        );

        return agentStatus;
    }

    private AppStatusInfo parseAppStatus(HashMap<String, Set<String>> checkAppStatus) {

        return AppStatusInfo.builder()
                .Healthy(checkAppStatus.get(AppStatusEnum.HEALTHY.getName()))
                .Failure(checkAppStatus.get(AppStatusEnum.FAILURE.getName()))
                .NotInstall(checkAppStatus.get(AppStatusEnum.NOT_INSTALL.getName()))
                .build();
    }

    /**
     * when server first time boot up
     * the server info are not collected completely
     * this will be executed to update or complete the octopus agent server info
     */
//    @Scheduled(initialDelay = 180000)
//    public void updateAgentServerInfo(){
//
//
//
//    }

    // agent boot up 120s then start to report its status
    // at the fix rate of 15s
    /*@Scheduled(initialDelay = ReportInitDelay, fixedRate = ReportFixedRate)*/
    public void sendAgentStatusToRedis() {

        try {

            String statusStreamKey = agentServerInfo.getServerName() + "-status";

            Map<String, String> map = Map.of(TimeUtils.currentTimeString(), objectMapper.writeValueAsString(collect()));

            StringRecord stringRecord = StreamRecords.string(map).withStreamKey(statusStreamKey);

            log.debug("Agent Status is ==> {}", map);
            redisTemplate.opsForStream().add(stringRecord);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
