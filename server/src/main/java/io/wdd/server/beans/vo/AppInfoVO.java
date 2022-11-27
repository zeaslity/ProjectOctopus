package io.wdd.server.beans.vo;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * (AppInfo)实体类对应的VO类
 *
 * @author icederce
 * @since 2022-11-26 11:42:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class AppInfoVO  {

    private String appName;
    
    private String appInfo;
    
    private String appVersion;
    /**
     * app associated domain name
     */
    private String appDomainName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    
    private String commont;
    /**
     * 0 alive || 1 deleted
     */
    private Integer isDelete;

}

