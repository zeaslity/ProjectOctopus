package io.wdd.agent.status.hardware.cpu;

import oshi.hardware.CentralProcessor;

import java.text.DecimalFormat;

public class CpuInfo {


    private static final DecimalFormat LOAD_FORMAT = new DecimalFormat("#.00");

    /**
     * CPU核心数
     */
    private Integer cpuNum;

    /**
     * CPU总的使用率
     */
    private double toTal;

    /**
     * CPU系统使用率
     */
    private double sys;

    /**
     * CPU用户使用率
     */
    private double user;

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

        this.cpuNum = processor.getLogicalProcessorCount();
        this.cpuModel = processor.toString();

        final long totalCpu = ticks.totalCpu();
        this.toTal = totalCpu;
        this.sys = formatDouble(ticks.cSys, totalCpu);
        this.user = formatDouble(ticks.user, totalCpu);
        this.wait = formatDouble(ticks.ioWait, totalCpu);
        this.free = formatDouble(ticks.idle, totalCpu);
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
