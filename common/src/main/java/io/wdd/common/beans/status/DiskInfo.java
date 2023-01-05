package io.wdd.common.beans.status;

import io.wdd.common.utils.FormatUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import oshi.hardware.HWDiskStore;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class DiskInfo {

    String name;

    String model;

    String serial;

    String size;


    private List<PartitionInfo> partitionInfoList;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    private static class PartitionInfo{

        String path;

        String type;

        String size;

        String mountPoint;

    }

    public static List<DiskInfo> mapFromDiskStore(List<HWDiskStore> hwDiskStoreList){

        return hwDiskStoreList.stream().map(
                hwDiskStore -> DiskInfo.builder()
                        .name(hwDiskStore.getName())
                        .model(hwDiskStore.getModel())
                        .serial(hwDiskStore.getSerial())
                        .size(FormatUtils.formatData(hwDiskStore.getSize()))
                        .partitionInfoList(
                                // partition should also be got from stream
                                hwDiskStore.getPartitions().stream().map(partition -> DiskInfo.PartitionInfo.builder()
                                        .path(partition.getIdentification())
                                        .size(FormatUtils.formatData(partition.getSize()))
                                        .type(partition.getType())
                                        .mountPoint(partition.getMountPoint())
                                        .build()
                                ).collect(Collectors.toList())
                        )
                        .build()
        ).collect(Collectors.toList());

    }


}
