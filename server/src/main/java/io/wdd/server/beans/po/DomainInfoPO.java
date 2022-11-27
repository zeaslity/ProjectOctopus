package io.wdd.server.beans.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName domain_info
 */
@TableName(value ="domain_info")
@Data
public class DomainInfoPO implements Serializable {
    /**
     * 
     */
    @TableId
    private Long domainId;

    /**
     * complete domain url
     */
    private String domainName;

    /**
     * domain provider name
     */
    private String domainProvider;

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

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
     * the dns record to the server ip
     */
    private String dnsIp;

    /**
     * domain dns provider name
     */
    private String dnsProvider;

    /**
     * dns type for A AAAA CNAME

     */
    private String dnsType;

    /**
     * 
     */
    private String dnsManageApi;

    /**
     * 
     */
    private Integer isDelete;

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
        DomainInfoPO other = (DomainInfoPO) that;
        return (this.getDomainId() == null ? other.getDomainId() == null : this.getDomainId().equals(other.getDomainId()))
            && (this.getDomainName() == null ? other.getDomainName() == null : this.getDomainName().equals(other.getDomainName()))
            && (this.getDomainProvider() == null ? other.getDomainProvider() == null : this.getDomainProvider().equals(other.getDomainProvider()))
            && (this.getRegisterTime() == null ? other.getRegisterTime() == null : this.getRegisterTime().equals(other.getRegisterTime()))
            && (this.getExpireTime() == null ? other.getExpireTime() == null : this.getExpireTime().equals(other.getExpireTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getDnsIp() == null ? other.getDnsIp() == null : this.getDnsIp().equals(other.getDnsIp()))
            && (this.getDnsProvider() == null ? other.getDnsProvider() == null : this.getDnsProvider().equals(other.getDnsProvider()))
            && (this.getDnsType() == null ? other.getDnsType() == null : this.getDnsType().equals(other.getDnsType()))
            && (this.getDnsManageApi() == null ? other.getDnsManageApi() == null : this.getDnsManageApi().equals(other.getDnsManageApi()))
            && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDomainId() == null) ? 0 : getDomainId().hashCode());
        result = prime * result + ((getDomainName() == null) ? 0 : getDomainName().hashCode());
        result = prime * result + ((getDomainProvider() == null) ? 0 : getDomainProvider().hashCode());
        result = prime * result + ((getRegisterTime() == null) ? 0 : getRegisterTime().hashCode());
        result = prime * result + ((getExpireTime() == null) ? 0 : getExpireTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getDnsIp() == null) ? 0 : getDnsIp().hashCode());
        result = prime * result + ((getDnsProvider() == null) ? 0 : getDnsProvider().hashCode());
        result = prime * result + ((getDnsType() == null) ? 0 : getDnsType().hashCode());
        result = prime * result + ((getDnsManageApi() == null) ? 0 : getDnsManageApi().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", domainId=").append(domainId);
        sb.append(", domainName=").append(domainName);
        sb.append(", domainProvider=").append(domainProvider);
        sb.append(", registerTime=").append(registerTime);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", dnsIp=").append(dnsIp);
        sb.append(", dnsProvider=").append(dnsProvider);
        sb.append(", dnsType=").append(dnsType);
        sb.append(", dnsManageApi=").append(dnsManageApi);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}