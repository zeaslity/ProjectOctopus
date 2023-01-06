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
