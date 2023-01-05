package io.wdd.common.beans.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
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
     */
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
    private CpuModel cpuModel;

    private double[] cpuLoadAverage;

    private double[] systemLoadAverage;

    public CpuInfo(CentralProcessor processor, long waitingTime) {
        this.init(processor, waitingTime);
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

    private static double formatDouble(double doubleNum) {

        return Double.parseDouble(LOAD_FORMAT.format(doubleNum));
    }

    private static double[] formatCpuLoadAverage(double[] cpuLoadAverage) {
        double[] result = new double[cpuLoadAverage.length];

        for (int i = 0; i < cpuLoadAverage.length; i++) {
            result[i] = formatDouble(cpuLoadAverage[i]);
        }

        return result;
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
        //this.ticks = ticks;

        this.cpuTotal = processor.getLogicalProcessorCount();
        this.coreTotal = processor.getPhysicalProcessorCount();

        this.cpuModel = mapFromProcessorIdentifier(processor.getProcessorIdentifier());

        final long totalCpu = ticks.totalCpu();
        this.cpuUsageTotol = totalCpu;

        this.systemCpuUsage = formatDouble(ticks.cSys, totalCpu);
        this.userCpuUsage = formatDouble(ticks.user, totalCpu);

        this.wait = formatDouble(ticks.ioWait, totalCpu);
        this.free = formatDouble(ticks.idle, totalCpu);


        // system load average
        this.systemLoadAverage = processor.getSystemLoadAverage(3);

        // cpu load average
        this.cpuLoadAverage = formatCpuLoadAverage(processor.getProcessorCpuLoad(waitingTime));

    }

    private CpuModel mapFromProcessorIdentifier(CentralProcessor.ProcessorIdentifier id) {

        return CpuModel.builder()
                .cpu64bit(id.isCpu64bit())
                .name(id.getName())
                .identifier(id.getIdentifier())
                .microArch(id.getMicroarchitecture())
                .vendor(id.getVendor())
                .build();
    }

    /**
     * CPU型号信息
     */
    //private CpuTicks ticks;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    private static class CpuModel {
        String name;

        String vendor;

        String microArch;

        boolean cpu64bit;

        String identifier;
    }

}
