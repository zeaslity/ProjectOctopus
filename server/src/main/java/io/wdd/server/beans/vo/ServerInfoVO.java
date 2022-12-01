package io.wdd.server.beans.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ServerInfoVO {

    /**
     * server host name
     */
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * server location , type City Country
     */
    private String location;

    /**
     * server isp manager
     */
    private String provider;

    /**
     * split by ,
     */
    private String managePort;

    /**
     *
     */
    private String cpuBrand;

    /**
     *
     */
    private String cpuCore;

    /**
     *
     */
    private String memoryTotal;

    /**
     *
     */
    private String diskTotal;

    /**
     *
     */
    private String diskUsage;

    /**
     *
     */
    private String ioSpeed;

    /**
     *
     */
    private String tcpControl;

    /**
     * server virtualization method
     */
    private String virtualization;

    /**
     *
     */
    private String osInfo;

    /**
     *
     */
    private String osKernelInfo;

    /**
     * machine uuid from /etc/machineid
     */
    private String machineId;

    /**
     * octopus message unique key name
     */
    private String topicName;

    /**
     *
     */
    private String comment;

}
