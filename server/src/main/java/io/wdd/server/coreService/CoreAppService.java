package io.wdd.server.coreService;

import io.wdd.server.beans.vo.AppInfoVO;

import java.util.List;

public interface CoreAppService {


    AppInfoVO getAppInfo(Long appId, String appName);

    List<AppInfoVO> getAppInfoAll();


    boolean createAppInfo(AppInfoVO appInfoVO);


    boolean updateAppInfo(AppInfoVO appInfoVO);


    boolean deleteAppInfo(Long appId);


}
