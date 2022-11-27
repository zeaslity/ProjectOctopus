package io.wdd.server.coreService.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.google.errorprone.annotations.Var;
import io.wdd.server.beans.po.AppInfoPO;
import io.wdd.server.beans.po.ServerAppRelationPO;
import io.wdd.server.beans.po.ServerInfoPO;
import io.wdd.server.beans.vo.AppInfoVO;
import io.wdd.server.beans.vo.ServerInfoVO;
import io.wdd.server.coreService.CoreServerService;
import io.wdd.server.service.AppInfoService;
import io.wdd.server.service.ServerAppRelationService;
import io.wdd.server.service.ServerInfoService;
import io.wdd.server.utils.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CoreServerServiceImpl implements CoreServerService {

    @Resource
    ServerInfoService serverInfoService;

    @Resource
    ServerAppRelationService serverAppRelationService;

    @Resource
    AppInfoService appInfoService;



    @Override
    public List<ServerInfoPO> getServerInfoSingle(String serverName, String ipv4, String serverLocation) {

        // ignore if deleted !
        return new LambdaQueryChainWrapper<>(serverInfoService.getBaseMapper())
                .eq(StringUtils.isNoneEmpty(serverName), ServerInfoPO::getServerName, serverName)
                .eq(StringUtils.isNoneEmpty(ipv4), ServerInfoPO::getServerIpPbV4, ipv4)
                .eq(StringUtils.isNoneEmpty(serverLocation), ServerInfoPO::getLocation, serverLocation)
                .list();

    }

    @Override
    public List<ServerInfoVO> getServerInfoList() {

        List<ServerInfoPO> serverInfoPOWithOutDelete = serverInfoService.list();


        return covertServerPOtoVO(serverInfoPOWithOutDelete);
    }

    @Override
    public List<ServerInfoVO> getServerInfoListIncludeDelete() {

        // todo how to  solve the problem?
//        this.covertServerPOtoVO(serverInfoService.getAll());

        return null;
    }

    @Override
    public boolean createServerInfo(ServerInfoVO serverInfoVO) {

        ServerInfoPO serverInfoPO = new ServerInfoPO();
        BeanUtils.copyProperties(serverInfoVO, serverInfoPO);

        return serverInfoService.save(serverInfoPO);
    }

    @Override
    public boolean updateServerInfo(ServerInfoPO serverInfoPO) {

        if (serverInfoPO.getServerId() == null) {
            return false;
        }

        return new LambdaUpdateChainWrapper<>(serverInfoService.getBaseMapper())
                .eq(ServerInfoPO::getServerId, serverInfoPO.getServerId())
                .update(serverInfoPO);
    }

    @Override
    public boolean deleteServer(Long serverId, String serverName) {

        if (serverId == null && StringUtils.isBlank(serverName)) {
            return false;
        }

        // set isDelete = 1
        return new LambdaUpdateChainWrapper<>(serverInfoService.getBaseMapper())
                .eq(serverId != null, ServerInfoPO::getServerId, serverId)
                .eq(StringUtils.isNoneEmpty(serverName), ServerInfoPO::getServerName, serverName)
                .set(ServerInfoPO::getIsDelete, 1)
                .update();

    }

    @Override
    public List<AppInfoVO> getAllAppInfo(Long serverId) {

        // serverInfo --- server_app_relation --- appInfo

        List<ServerAppRelationPO> serverAppRelationPOList = new LambdaQueryChainWrapper<ServerAppRelationPO>(serverAppRelationService.getBaseMapper()).eq(ServerAppRelationPO::getServerId, serverId).list();


        // query the app info with specific server id
        List<AppInfoPO> appInfoPOList = appInfoService.listByIds(serverAppRelationPOList.stream().map(
                serverAppRelationPO -> {
                    return serverAppRelationPO.getAppId();
                }
        ).collect(Collectors.toList()));


        return EntityUtils.cvToTarget(appInfoPOList,AppInfoVO.class);
    }

    private List<ServerInfoVO> covertServerPOtoVO(List<ServerInfoPO> serverInfoPOList) {

        if (null == serverInfoPOList || serverInfoPOList.size() == 0) {
            return Collections.emptyList();
        }

        return serverInfoPOList.stream().map(serverInfoPO -> {
                    ServerInfoVO serverInfoVO = new ServerInfoVO();
                    BeanUtils.copyProperties(serverInfoPO, serverInfoVO);
                    return serverInfoVO;
                }
        ).collect(Collectors.toList());

    }
}
