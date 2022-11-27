package io.wdd.server.coreService;

import io.wdd.server.beans.po.ServerInfoPO;
import io.wdd.server.beans.vo.AppInfoVO;
import io.wdd.server.beans.vo.ServerInfoVO;

import java.util.List;

public interface CoreServerService {

    List<ServerInfoPO> getServerInfoSingle(String serverName, String ipv4, String serverLocation);

    List<ServerInfoVO> getServerInfoList();

    List<ServerInfoVO> getServerInfoListIncludeDelete();

    boolean createServerInfo(ServerInfoVO serverInfoVO);

    boolean updateServerInfo(ServerInfoPO serverInfoPO);

    boolean deleteServer(Long serverId, String serverName);

    List<AppInfoVO> getAllAppInfo(Long serverId);
}
