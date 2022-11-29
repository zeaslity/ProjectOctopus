package io.wdd.agent.initialization.beans;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Configuration
public class ServerInfo {

    @Value("${serverName}")
    private String serverName;

    /**
     * server public ipv4
     */
    @Value("${serverIpPbV4}")
    private String serverIpPbV4;

    /**
     * server inner ipv4
     */
    @Value("${serverIpInV4}")
    private String serverIpInV4;

    /**
     * server public ipv6
     */
    @Value("${serverIpPbV6}")
    private String serverIpPbV6;

    /**
     * server inner ipv6
     */
    @Value("${serverIpInV6}")
    private String serverIpInV6;

    /**
     *
     */
    @Value("${location}")
    private String location;

    /**
     *
     */
    @Value("${provider}")
    private String provider;

    /**
     * split by ,
     */
    @Value("${managePort}")
    private String managePort;

    /**
     *
     */
    @Value("${cpuCore}")
    private String cpuCore;

    /**
     *
     */
    @Value("${cpuBrand}")
    private String cpuBrand;

    /**
     *
     */
    @Value("${osInfo}")
    private String osInfo;

    /**
     *
     */
    @Value("${osKernelInfo}")
    private String osKernelInfo;

    @Value("${tcpControl}")
    private String tcpControl;

    @Value("${virtualization}")
    private String virtualization;

    @Value("${ioSpeed}")
    private String ioSpeed;

    @Value("${memoryTotal}")
    private String memoryTotal;

    @Value("${diskTotal}")
    private String diskTotal;

    @Value("${diskUsage}")
    private String diskUsage;

    /**
     *
     */
    @Value("${serverIpPbV4}")
    private String comment;


    @Value("${machineId}")
    private String machineId;

}
