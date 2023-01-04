package io.wdd.agent.status.redisReporter;


import io.wdd.agent.status.hardware.cpu.CpuInfo;
import io.wdd.agent.status.hardware.memory.MemoryInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;

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

    List<HWDiskStore> diskStoreInfo;

    List<NetworkIF> networkInfo;

}
