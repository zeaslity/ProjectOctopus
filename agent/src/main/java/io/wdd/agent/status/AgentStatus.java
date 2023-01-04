package io.wdd.agent.status;


import io.wdd.agent.status.hardware.CpuInfo;
import io.wdd.agent.status.hardware.MemoryInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

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

    OperatingSystem osInfo;

}
