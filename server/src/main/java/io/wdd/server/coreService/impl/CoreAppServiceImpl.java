package io.wdd.server.coreService.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import io.wdd.server.beans.po.AppInfoPO;
import io.wdd.server.beans.vo.AppInfoVO;
import io.wdd.server.coreService.CoreAppService;
import io.wdd.server.service.AppInfoService;
import io.wdd.server.utils.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class CoreAppServiceImpl implements CoreAppService {

    @Resource
    AppInfoService appInfoService;

    @Override
    public AppInfoVO appGetSingle(Long appId, String appName) {

        List<AppInfoPO> appInfoPOList = new LambdaQueryChainWrapper<AppInfoPO>(appInfoService.getBaseMapper())
                .eq(appId != null, AppInfoPO::getAppId, appId)
                .eq(StringUtils.isNoneEmpty(appName), AppInfoPO::getAppName, appName)
                .list();

        List<AppInfoVO> appInfoPVOList = EntityUtils.cvToTarget(appInfoPOList, AppInfoVO.class);

        return appInfoPVOList.get(0);
    }

    @Override
    public List<AppInfoVO> appGetAll() {

        return EntityUtils.cvToTarget(appInfoService.list(), AppInfoVO.class);
    }

    @Override
    public boolean appCreate(AppInfoVO appInfoVO) {


        return appInfoService.save(
                EntityUtils.cvToTarget(appInfoVO,AppInfoPO.class)
        );
    }

    @Override
    public boolean updateAppInfo(AppInfoVO appInfoVO) {

        return appInfoService.updateById(
                EntityUtils.cvToTarget(appInfoVO,AppInfoPO.class)
        );
    }

    @Override
    public boolean appDelete(Long appId) {

        return appInfoService.removeById(appId);
    }
}
