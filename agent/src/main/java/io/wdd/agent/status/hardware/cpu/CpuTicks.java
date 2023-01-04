package io.wdd.agent.status.hardware.cpu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oshi.hardware.CentralProcessor;
import oshi.util.Util;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CpuTicks {

    long idle;

    long nice;

    long irq;

    long softIrq;

    long steal;

    long cSys;

    long user;

    long ioWait;

    private static int IDLEIndex;
    private static int NICEIndex;
    private static int IRQIndex;
    private static int SOFTIRQIndex;
    private static int STEALIndex;
    private static int SYSTEMIndex;
    private static int USERIndex;
    private static int IOWAITIndex;

    static {

        IDLEIndex = CentralProcessor.TickType.IDLE.getIndex();
        NICEIndex = CentralProcessor.TickType.NICE.getIndex();
        IRQIndex =CentralProcessor.TickType.IRQ.getIndex();
        SOFTIRQIndex = CentralProcessor.TickType.SOFTIRQ.getIndex();
        STEALIndex = CentralProcessor.TickType.STEAL.getIndex();
        SYSTEMIndex = CentralProcessor.TickType.SYSTEM.getIndex();
        USERIndex = CentralProcessor.TickType.USER.getIndex();
        IOWAITIndex = CentralProcessor.TickType.IOWAIT.getIndex();

    }

    /**
     * 构造，等待时间为用于计算在一定时长内的CPU负载情况，如传入1000表示最近1秒的负载情况
     *
     * @param processor   {@link CentralProcessor}
     * @param waitingTime 设置等待时间，单位毫秒
     */
    public CpuTicks(CentralProcessor processor, long waitingTime) {
        // CPU信息
        final long[] prevTicks = processor.getSystemCpuLoadTicks();

        // 这里必须要设置延迟
        Util.sleep(waitingTime);
        final long[] ticks = processor.getSystemCpuLoadTicks();

        this.idle = tick(prevTicks, ticks, IDLEIndex);
        this.nice = tick(prevTicks, ticks, NICEIndex);
        this.irq = tick(prevTicks, ticks, IRQIndex);
        this.softIrq = tick(prevTicks, ticks, SOFTIRQIndex);
        this.steal = tick(prevTicks, ticks, STEALIndex);
        this.cSys = tick(prevTicks, ticks, SYSTEMIndex);
        this.user = tick(prevTicks, ticks, USERIndex);
        this.ioWait = tick(prevTicks, ticks, IOWAITIndex);
    }

    /**
     * 获取CPU总的使用率
     *
     * @return CPU总使用率
     */
    public long totalCpu() {
        return Math.max(user + nice + cSys + idle + ioWait + irq + softIrq + steal, 0);
    }

    /**
     * 获取一段时间内的CPU负载标记差
     *
     * @param prevTicks 开始的ticks
     * @param ticks     结束的ticks
     * @param tickType  tick类型
     * @return 标记差
     * @since 5.7.12
     */
    private static long tick(long[] prevTicks, long[] ticks, int index) {
        return ticks[index] - prevTicks[index];
    }


}
