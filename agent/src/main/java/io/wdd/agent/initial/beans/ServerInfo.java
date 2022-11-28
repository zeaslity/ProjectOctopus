package io.wdd.agent.initial.beans;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ServerInfo {

    private String serverName;

    /**
     * server public ipv4
     */
    private String serverIpPbV4;

    /**
     * server inner ipv4
     */
    private String serverIpInV4;

    /**
     * server public ipv6
     */
    private String serverIpPbV6;

    /**
     * server inner ipv6
     */
    private String serverIpInV6;

    /**
     *
     */
    private String location;

    /**
     *
     */
    private String provider;

    /**
     * split by ,
     */
    private String managePort;

    /**
     *
     */
    private Integer cpuCore;

    /**
     *
     */
    private String cpuBrand;

    /**
     *
     */
    private String osInfo;

    /**
     *
     */
    private String osKernelInfo;


    private String tcpControl;


    private String virtualization;


    private String ioSpeed;

    private String memoryTotal;

    private String diskTotal;

    private String diskUsage;

    /**
     *
     */
    private String comment;


}
