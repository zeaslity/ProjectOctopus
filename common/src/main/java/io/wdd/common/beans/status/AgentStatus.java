package io.wdd.common.beans.status;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class AgentStatus {

    private static final String AGENT_STATUS_KEY_SUFFIX = "-Status";

    public static String getRedisStatusKey(String agentTopicName) {
        return agentTopicName+AGENT_STATUS_KEY_SUFFIX;
    }

    String time;

    String agentName;

    String agentTopicName;

    CpuInfo cpuInfo;

    MemoryInfo memoryInfo;

    List<DiskInfo> diskStoreInfo;

    List<NetworkInfo> networkInfo;

    AgentSystemInfo osInfo;

    AppStatusInfo appStatus;

}
