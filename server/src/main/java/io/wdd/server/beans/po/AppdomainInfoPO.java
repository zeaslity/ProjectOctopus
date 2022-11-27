package io.wdd.server.beans.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName appdomain_info
 */
@TableName(value ="appdomain_info")
@Data
public class AppdomainInfoPO implements Serializable {
    /**
     * 
     */
    @TableId
    private Long appDomainId;

    /**
     * app associated domain name
     */
    private String appDomainName;

    /**
     * app domain port
     */
    private String appDomainPort;

    /**
     * app associated domain_info id
     */
    private Long domainId;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 
     */
    private Integer idDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        AppdomainInfoPO other = (AppdomainInfoPO) that;
        return (this.getAppDomainId() == null ? other.getAppDomainId() == null : this.getAppDomainId().equals(other.getAppDomainId()))
            && (this.getAppDomainName() == null ? other.getAppDomainName() == null : this.getAppDomainName().equals(other.getAppDomainName()))
            && (this.getAppDomainPort() == null ? other.getAppDomainPort() == null : this.getAppDomainPort().equals(other.getAppDomainPort()))
            && (this.getDomainId() == null ? other.getDomainId() == null : this.getDomainId().equals(other.getDomainId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIdDelete() == null ? other.getIdDelete() == null : this.getIdDelete().equals(other.getIdDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAppDomainId() == null) ? 0 : getAppDomainId().hashCode());
        result = prime * result + ((getAppDomainName() == null) ? 0 : getAppDomainName().hashCode());
        result = prime * result + ((getAppDomainPort() == null) ? 0 : getAppDomainPort().hashCode());
        result = prime * result + ((getDomainId() == null) ? 0 : getDomainId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIdDelete() == null) ? 0 : getIdDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", appDomainId=").append(appDomainId);
        sb.append(", appDomainName=").append(appDomainName);
        sb.append(", appDomainPort=").append(appDomainPort);
        sb.append(", domainId=").append(domainId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", idDelete=").append(idDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}