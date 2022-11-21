package io.wdd.server.beans.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime registerTime;

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime expireTime;

    /**
     *
     */
    private String location;

    /**
     *
     */
    private String provider;

    /**
     *
     */
    @Nullable
    private Integer managePort;

    /**
     *
     */
    private Integer cpuCore;

    /**
     *
     */
    @Nullable
    private String cpuBrand;

    /**
     *
     */
    @Nullable
    private String osInfo;

    /**
     *
     */
    @Nullable
    private String osKernelInfo;

    /**
     *
     */
    @Nullable
    private String comment;


    /**
     * server is deleted or not ?
     */
    private Integer isDelete;


    private Integer version;

}
