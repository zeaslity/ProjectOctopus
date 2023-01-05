package io.wdd.common.beans.status;

import io.wdd.common.utils.FormatUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import oshi.hardware.GlobalMemory;
import oshi.hardware.VirtualMemory;


@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder(toBuilder = true)
public class MemoryInfo {


    String total;

    String usage;

    String available;

    String memoryType;

    String swapTotal;

    String swapAvailable;

    String swapUsage;

    public static MemoryInfo build(GlobalMemory memory) {

        VirtualMemory virtualMemory = memory.getVirtualMemory();
        return MemoryInfo.builder()
                .memoryType(memory.getPhysicalMemory().get(0).getMemoryType())
                .total(FormatUtils.formatData(memory.getTotal()))
                .available(FormatUtils.formatData(memory.getAvailable()))
                .usage(FormatUtils.formatData(memory.getTotal() - memory.getAvailable()))
                .swapTotal(FormatUtils.formatData(virtualMemory.getSwapTotal()))
                .swapUsage(FormatUtils.formatData(virtualMemory.getSwapUsed()))
                .swapAvailable(FormatUtils.formatData(virtualMemory.getSwapTotal() - virtualMemory.getSwapUsed()))
                .build();
    }

}
