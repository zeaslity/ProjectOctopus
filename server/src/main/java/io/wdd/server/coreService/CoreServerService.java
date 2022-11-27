package io.wdd.server.coreService;

import io.wdd.server.beans.po.ServerInfoPO;
import io.wdd.server.beans.vo.AppInfoVO;
import io.wdd.server.beans.vo.ServerInfoVO;

import java.util.List;

public interface CoreServerService {

    List<ServerInfoPO> serverGetSingle(String serverName, String ipv4, String serverLocation);

    List<ServerInfoVO> serverGetAll();

    List<ServerInfoVO> serverGetAllIncludeDelete();

    boolean serverCreate(ServerInfoVO serverInfoVO);

    boolean serverUpdate(ServerInfoPO serverInfoPO);

    boolean serverDelete(Long serverId, String serverName);

    List<AppInfoVO> appGetAll(Long serverId);

    AppInfoVO appCreate(Long serverId, AppInfoVO appInfoVO);

    boolean appDelete(Long serverId, Long appId);
}
