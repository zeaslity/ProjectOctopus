package io.wdd.common.beans.status;

import lombok.Data;

/**
 * 没时间整这些，反正大一点数据也无所谓 不是吗
 */
@Deprecated
@Data
public class MetricStatus {

    CpuInfo cpuInfo;

    MemoryInfo memoryInfo;

    AppStatusInfo appStatus;

}
