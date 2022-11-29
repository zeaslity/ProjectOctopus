package io.wdd.server.controller;


import io.wdd.server.beans.vo.AppInfoVO;
import io.wdd.server.coreService.CoreAppService;
import io.wdd.common.beans.response.R;
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

    @GetMapping("/appGetAll")
    public R<List<AppInfoVO>> appGetAll() {

        return R.ok(coreAppService.appGetAll());
    }

    @GetMapping("/appGetSingle")
    public R<AppInfoVO> appGetSingle(
            @RequestParam(value = "appId", required = false) @Nullable Long appId,
            @RequestParam(value = "appName", required = false) @Nullable String appName
    ) {

        return R.ok(coreAppService.appGetSingle(appId, appName));
    }


    @PostMapping("/appCreate")
    public R<String> appCreate(
            @RequestBody @Validated AppInfoVO appInfoVO) {

        if (coreAppService.appCreate(appInfoVO)) {
            return R.ok("App created successfully !");
        }



        return R.failed("App created failed !");
    }


    @PostMapping("/appDelete")
    public R<String> appDelete(
            @RequestParam(value = "appId") Long appId
    ){

        if (coreAppService.appDelete(appId)) {
            return R.ok("app delete successfully !");
        }

        return R.failed("App delete failed !");
    }

    /*
    * app --- appDomain
    * 1 ------ n
    *  a domain is often refer to an app
    * */

    // get

    // create

    // update

    // delete

}
