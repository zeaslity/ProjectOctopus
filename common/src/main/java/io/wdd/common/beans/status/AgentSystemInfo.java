package io.wdd.common.beans.status;

import io.wdd.common.utils.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class AgentSystemInfo {

    String osInfo;

    String family;

    String manufacturer;

    String bootTime;

    String upTime;

    public static AgentSystemInfo mapFromOHSISystem(OperatingSystem os) {

        return AgentSystemInfo.builder()
                .osInfo(String.valueOf(os.getVersionInfo()))
                .family(os.getFamily())
                .manufacturer(os.getManufacturer())
                .bootTime(TimeUtils.localDateTimeString(
                        LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(os.getSystemBootTime()),
                                ZoneId.of("UTC+8")
                        )
                ))
                .upTime(TimeUtils.toRelative(os.getSystemUptime()*1000, 3))
                .build();

    }


}
