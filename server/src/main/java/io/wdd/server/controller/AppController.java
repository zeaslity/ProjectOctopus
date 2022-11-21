package io.wdd.server.controller;


import io.wdd.server.beans.vo.AppInfoVO;
import io.wdd.server.coreService.CoreAppService;
import io.wdd.wddcommon.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/app")
public class AppController {

    @Resource
    CoreAppService coreAppService;

    @GetMapping("/all")
    public R<List<AppInfoVO>> getAllAppInfo() {

        return R.ok(coreAppService.getAppInfoAll());
    }

    @GetMapping("/single")
    public R<AppInfoVO> getAppInfo(
            @RequestParam(value = "appId", required = false) @Nullable Long appId,
            @RequestParam(value = "appName", required = false) @Nullable String appName
    ) {

        return R.ok(coreAppService.getAppInfo(appId, appName));
    }


    @PostMapping("/new")
    public R createNewApp(
            @RequestBody @Validated AppInfoVO appInfoVO) {

        if (coreAppService.createAppInfo(appInfoVO)) {
            return R.ok("App created successfully !");
        }



        return R.failed("App created failed !");
    }


    @PostMapping("/delete")
    public R deleteApp(
            @RequestParam(value = "appId") Long appId
    ){

        if (coreAppService.deleteAppInfo(appId)) {
            return R.ok("app delete successfully !");
        }

        return R.failed("App delete failed !");
    }

}
