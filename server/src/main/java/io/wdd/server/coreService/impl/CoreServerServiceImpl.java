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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    public List<ServerInfoPO> serverGetSingle(String serverName, String ipv4, String serverLocation) {

        // ignore if deleted !
        return new LambdaQueryChainWrapper<>(serverInfoService.getBaseMapper())
                .eq(StringUtils.isNoneEmpty(serverName), ServerInfoPO::getServerName, serverName)
                .eq(StringUtils.isNoneEmpty(ipv4), ServerInfoPO::getServerIpPbV4, ipv4)
                .eq(StringUtils.isNoneEmpty(serverLocation), ServerInfoPO::getLocation, serverLocation)
                .list();

    }

    @Override
    public List<ServerInfoVO> serverGetAll() {

        List<ServerInfoPO> serverInfoPOWithOutDelete = serverInfoService.list();


        return covertServerPOtoVO(serverInfoPOWithOutDelete);
    }

    @Override
    public List<ServerInfoVO> serverGetAllIncludeDelete() {

        // todo how to  solve the problem?
//        this.covertServerPOtoVO(serverInfoService.getAll());

        return null;
    }

    @Override
    public boolean serverCreate(ServerInfoVO serverInfoVO) {

        ServerInfoPO serverInfoPO = new ServerInfoPO();
        BeanUtils.copyProperties(serverInfoVO, serverInfoPO);

        return serverInfoService.save(serverInfoPO);
    }

    @Override
    public boolean serverUpdate(ServerInfoPO serverInfoPO) {

        if (serverInfoPO.getServerId() == null) {
            return false;
        }

        return new LambdaUpdateChainWrapper<>(serverInfoService.getBaseMapper())
                .eq(ServerInfoPO::getServerId, serverInfoPO.getServerId())
                .update(serverInfoPO);
    }

    @Override
    public boolean serverDelete(Long serverId, String serverName) {

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
    public List<AppInfoVO> appGetAll(Long serverId) {

        // serverInfo --- server_app_relation --- appInfo

        List<ServerAppRelationPO> serverAppRelationPOList = new LambdaQueryChainWrapper<ServerAppRelationPO>(serverAppRelationService.getBaseMapper()).eq(ServerAppRelationPO::getServerId, serverId).list();

        Assert.notEmpty(serverAppRelationPOList,"No server find");

        // query the app info with specific server id
        List<AppInfoPO> appInfoPOList = appInfoService.listByIds(serverAppRelationPOList.stream().map(
                serverAppRelationPO -> serverAppRelationPO.getAppId()
        ).collect(Collectors.toList()));


        return EntityUtils.cvToTarget(appInfoPOList,AppInfoVO.class);
    }

    @Override
    @Transactional
    public AppInfoVO appCreate(Long serverId, AppInfoVO appInfoVO) {

        Assert.notNull(serverInfoService.getById(serverId),"server not find, can't create a app");

        // 1- save appInfo itself
        AppInfoPO appInfoPO = EntityUtils.cvToTarget(appInfoVO, AppInfoPO.class);
        appInfoService.save(appInfoPO);

        // 2. create the relation
        ServerAppRelationPO relationPO = new ServerAppRelationPO();
        relationPO.setServerId(serverId);
        relationPO.setAppId(appInfoPO.getAppId());
        serverAppRelationService.save(relationPO);


        return EntityUtils.cvToTarget(appInfoPO, AppInfoVO.class);
    }

    @Override
    @Transactional
    public boolean appDelete(Long serverId, Long appId) {

        Assert.notNull(serverInfoService.getById(serverId),"server not find, can't delete a app");
        Assert.notNull(appInfoService.getById(appId),"app not find, can't delete a app");

        // 1. delete the relation
        serverAppRelationService.removeById(serverId);
        // 2. delete the app
        appInfoService.removeById(appId);

        return true;
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
