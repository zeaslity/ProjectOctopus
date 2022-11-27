package io.wdd.server.coreService;

import io.wdd.server.beans.vo.AppInfoVO;

import java.util.List;

public interface CoreAppService {


    AppInfoVO appGetSingle(Long appId, String appName);

    List<AppInfoVO> appGetAll();


    boolean appCreate(AppInfoVO appInfoVO);


    boolean updateAppInfo(AppInfoVO appInfoVO);


    boolean appDelete(Long appId);


}
