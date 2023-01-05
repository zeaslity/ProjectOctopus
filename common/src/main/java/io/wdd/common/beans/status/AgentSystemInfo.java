package io.wdd.common.beans.status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import oshi.software.os.OperatingSystem;

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
                .bootTime()

    }


}
