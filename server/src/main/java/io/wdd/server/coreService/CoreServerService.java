package io.wdd.server.coreService;

import io.wdd.server.beans.po.DomainInfoPO;
import io.wdd.server.beans.po.ServerInfoPO;
import io.wdd.server.beans.vo.AppInfoVO;
import io.wdd.server.beans.vo.DomainInfoVO;
import io.wdd.server.beans.vo.ServerInfoVO;

import java.util.List;
import java.util.Set;

public interface CoreServerService {

    List<ServerInfoPO> serverGetSingle(String serverName, String ipv4, String serverLocation);

    List<ServerInfoVO> serverGetAll();

    List<ServerInfoVO> serverGetAllIncludeDelete();

    boolean serverCreate(ServerInfoVO serverInfoVO);

    boolean serverCreateOrUpdate(ServerInfoVO serverInfoVO);

    boolean serverUpdate(ServerInfoPO serverInfoPO);

    boolean serverDelete(Long serverId, String serverName);

    List<AppInfoVO> appGetAll(Long serverId);

    AppInfoVO appCreate(Long serverId, AppInfoVO appInfoVO);

    boolean appDelete(Long serverId, Long appId);

    List<DomainInfoVO> domainGetAll(Long serverId);

    List<DomainInfoVO>  domainGetSingle(Long serverId, String domainName, String dnsIP);


    boolean domainCreate(Long serverId, DomainInfoVO domainInfoVO);

    boolean domainUpdate(DomainInfoPO domainInfoPO);


    boolean domainDelete(Long serverId, Long domainId);

}
