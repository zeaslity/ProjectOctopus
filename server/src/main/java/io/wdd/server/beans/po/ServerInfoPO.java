package io.wdd.server.beans.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName server_info
 */
@TableName(value = "server_info")
@Data
public class ServerInfoPO implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * server primary key
     */
    @TableId(type = IdType.AUTO)
    private Long serverId;
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
    private LocalDateTime registerTime;
    /**
     *
     */
    private LocalDateTime expireTime;
    /**
     *
     */
    private LocalDateTime updateTime;
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
    /**
     *
     */
    private String comment;
    /**
     * 0 alive || 1 deleted
     */
    private Integer isDelete;
    /**
     * optimistic lock for concurrent
     */
    private Integer version;

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
        ServerInfoPO other = (ServerInfoPO) that;
        return (this.getServerId() == null ? other.getServerId() == null : this.getServerId().equals(other.getServerId()))
                && (this.getServerName() == null ? other.getServerName() == null : this.getServerName().equals(other.getServerName()))
                && (this.getServerIpPbV4() == null ? other.getServerIpPbV4() == null : this.getServerIpPbV4().equals(other.getServerIpPbV4()))
                && (this.getServerIpInV4() == null ? other.getServerIpInV4() == null : this.getServerIpInV4().equals(other.getServerIpInV4()))
                && (this.getServerIpPbV6() == null ? other.getServerIpPbV6() == null : this.getServerIpPbV6().equals(other.getServerIpPbV6()))
                && (this.getServerIpInV6() == null ? other.getServerIpInV6() == null : this.getServerIpInV6().equals(other.getServerIpInV6()))
                && (this.getRegisterTime() == null ? other.getRegisterTime() == null : this.getRegisterTime().equals(other.getRegisterTime()))
                && (this.getExpireTime() == null ? other.getExpireTime() == null : this.getExpireTime().equals(other.getExpireTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
                && (this.getProvider() == null ? other.getProvider() == null : this.getProvider().equals(other.getProvider()))
                && (this.getManagePort() == null ? other.getManagePort() == null : this.getManagePort().equals(other.getManagePort()))
                && (this.getCpuCore() == null ? other.getCpuCore() == null : this.getCpuCore().equals(other.getCpuCore()))
                && (this.getCpuBrand() == null ? other.getCpuBrand() == null : this.getCpuBrand().equals(other.getCpuBrand()))
                && (this.getOsInfo() == null ? other.getOsInfo() == null : this.getOsInfo().equals(other.getOsInfo()))
                && (this.getOsKernelInfo() == null ? other.getOsKernelInfo() == null : this.getOsKernelInfo().equals(other.getOsKernelInfo()))
                && (this.getComment() == null ? other.getComment() == null : this.getComment().equals(other.getComment()))
                && (this.getIsDelete() == null ? other.getIsDelete() == null : this.getIsDelete().equals(other.getIsDelete()))
                && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getServerId() == null) ? 0 : getServerId().hashCode());
        result = prime * result + ((getServerName() == null) ? 0 : getServerName().hashCode());
        result = prime * result + ((getServerIpPbV4() == null) ? 0 : getServerIpPbV4().hashCode());
        result = prime * result + ((getServerIpInV4() == null) ? 0 : getServerIpInV4().hashCode());
        result = prime * result + ((getServerIpPbV6() == null) ? 0 : getServerIpPbV6().hashCode());
        result = prime * result + ((getServerIpInV6() == null) ? 0 : getServerIpInV6().hashCode());
        result = prime * result + ((getRegisterTime() == null) ? 0 : getRegisterTime().hashCode());
        result = prime * result + ((getExpireTime() == null) ? 0 : getExpireTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getProvider() == null) ? 0 : getProvider().hashCode());
        result = prime * result + ((getManagePort() == null) ? 0 : getManagePort().hashCode());
        result = prime * result + ((getCpuCore() == null) ? 0 : getCpuCore().hashCode());
        result = prime * result + ((getCpuBrand() == null) ? 0 : getCpuBrand().hashCode());
        result = prime * result + ((getOsInfo() == null) ? 0 : getOsInfo().hashCode());
        result = prime * result + ((getOsKernelInfo() == null) ? 0 : getOsKernelInfo().hashCode());
        result = prime * result + ((getComment() == null) ? 0 : getComment().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        return result;
    }

    @Override
    public String toString() {
        String sb = getClass().getSimpleName() +
                " [" +
                "Hash = " + hashCode() +
                ", serverId=" + serverId +
                ", serverName=" + serverName +
                ", serverIpPbV4=" + serverIpPbV4 +
                ", serverIpInV4=" + serverIpInV4 +
                ", serverIpPbV6=" + serverIpPbV6 +
                ", serverIpInV6=" + serverIpInV6 +
                ", registerTime=" + registerTime +
                ", expireTime=" + expireTime +
                ", updateTime=" + updateTime +
                ", location=" + location +
                ", provider=" + provider +
                ", managePort=" + managePort +
                ", cpuCore=" + cpuCore +
                ", cpuBrand=" + cpuBrand +
                ", osInfo=" + osInfo +
                ", osKernelInfo=" + osKernelInfo +
                ", comment=" + comment +
                ", isDelete=" + isDelete +
                ", version=" + version +
                ", serialVersionUID=" + serialVersionUID +
                "]";
        return sb;
    }
}