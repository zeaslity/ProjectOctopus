package io.wdd.common.beans.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import oshi.hardware.CentralProcessor;

import java.text.DecimalFormat;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CpuInfo {


    private static final DecimalFormat LOAD_FORMAT = new DecimalFormat("#.00");

    /**
     * CPU总线程
     */
    private Integer cpuTotal;
    
    /**
    * CPU核心数
    * */
    private Integer coreTotal;

    /**
     * CPU总数  计算方式理论上为 cpuTotal * 100
     */
    private double cpuUsageTotol;

    /**
     * CPU系统使用率
     */
    private double systemCpuUsage;

    /**
     * CPU用户使用率
     */
    private double userCpuUsage;

    /**
     * CPU当前等待率
     */
    private double wait;

    /**
     * CPU当前空闲率
     */
    private double free;

    /**
     * CPU型号信息
     */
    private String cpuModel;

    private double[] cpuLoadAverage;

    private double[] systemLoadAverage;

    /**
     * CPU型号信息
     */
    private CpuTicks ticks;


    public CpuInfo(CentralProcessor processor, long waitingTime){
        this.init(processor, waitingTime);
    }


    /**
     * 获取指定等待时间内系统CPU 系统使用率、用户使用率、利用率等等 相关信息
     *
     * @param processor   {@link CentralProcessor}
     * @param waitingTime 设置等待时间，单位毫秒
     * @since 5.7.12
     */
    private void init(CentralProcessor processor, long waitingTime) {

        final CpuTicks ticks = new CpuTicks(processor, waitingTime);
        this.ticks = ticks;

        this.cpuTotal = processor.getLogicalProcessorCount();
        this.coreTotal = processor.getPhysicalProcessorCount();

        this.cpuModel = processor.toString();

        final long totalCpu = ticks.totalCpu();
        this.cpuUsageTotol = totalCpu;

        this.systemCpuUsage = formatDouble(ticks.cSys, totalCpu);
        this.userCpuUsage = formatDouble(ticks.user, totalCpu);

        this.wait = formatDouble(ticks.ioWait, totalCpu);
        this.free = formatDouble(ticks.idle, totalCpu);


        // system load average
        this.systemLoadAverage = processor.getSystemLoadAverage(3);

        // cpu load average
        this.cpuLoadAverage = processor.getProcessorCpuLoad(waitingTime);

    }

    /**
     * 获取每个CPU核心的tick，计算方式为 100 * tick / totalCpu
     *
     * @param tick     tick
     * @param totalCpu CPU总数
     * @return 平均每个CPU核心的tick
     * @since 5.7.12
     */
    private static double formatDouble(long tick, long totalCpu) {
        if (0 == totalCpu) {
            return 0D;
        }
        return Double.parseDouble(LOAD_FORMAT.format(tick <= 0 ? 0 : (100d * tick / totalCpu)));
    }

}
