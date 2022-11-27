package io.wdd.server.controller;


import io.wdd.server.beans.vo.AppInfoVO;
import io.wdd.wddcommon.utils.R;
import io.wdd.server.beans.po.ServerInfoPO;
import io.wdd.server.beans.vo.ServerInfoVO;
import io.wdd.server.coreService.CoreServerService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/server")
public class ServerController {

    @Autowired
    CoreServerService coreServerService;

    @GetMapping("/all")
    public R<List> serverGetAll() {

        return R.ok(coreServerService.serverGetAll());
    }

    @GetMapping("/allIncludeDelete")
    public R<List> serverGetAllIncludeDelete() {

        return R.ok(coreServerService.serverGetAllIncludeDelete());
    }

    @PostMapping("/single")
    public R serverGetSingle(
            @RequestParam(value = "serverIPv4") @Nullable String ipv4,
            @RequestParam(value = "serverName") @Nullable String serverName,
            @RequestParam(value = "serverLocation") @Nullable String serverLocation
    ) {
        return R.ok(coreServerService.serverGetSingle(serverName, ipv4, serverLocation));
    }

    @PostMapping("/serverCreate")
    public R serverCreate(@RequestBody @Validated ServerInfoVO serverInfoVO) {

        if (coreServerService.serverCreate(serverInfoVO)) {
            return R.ok("Create Server Success !");
        }

        return R.failed("Create Server Failed !");
    }

    @PostMapping("/serverUpdate")
    public R serverUpdate(@RequestBody ServerInfoPO serverInfoPO) {

        if (coreServerService.serverUpdate(serverInfoPO)) {
            return R.ok("Server info update successfully !");
        }

        return R.failed("Server info update failed !");
    }

    @PostMapping("/serverDelete")
    public R<String> serverDelete(
            @RequestParam(value = "serverId") @Nullable Long serverId,
            @RequestParam(value = "serverName") @Nullable String serverName) {

        if (coreServerService.serverDelete(serverId, serverName)) {
            R.ok("Delete Server Successfully !");
        }

        return R.failed("Delete Server Failed !");
    }


    /*
     * Associated with appInfo
     *  server 1______n app
     * */

    // get
    @GetMapping("/appGetAll")
    public R<List<AppInfoVO>> appGetAll(
            @RequestParam(value = "serverId", required = true) Long serverId
    ) {

        return R.ok(coreServerService.appGetAll(serverId));
    }

    // create
    @PostMapping("/appCreate")
    public R<AppInfoVO> appCreate(
            @RequestParam(value = "serverId", required = true) Long serverId,
            @RequestBody @Validated AppInfoVO appInfoVO
    ) {

        AppInfoVO newAppForServer = coreServerService.appCreate(serverId, appInfoVO);

        if (ObjectUtils.isNotEmpty(newAppForServer)) {
            return R.ok(newAppForServer);
        }

        System.out.println("create new app failed !");

        return R.failed(null);
    }

    // delete
    @PostMapping("/appDelete")
    public R<String> appDelete(
            @RequestParam(value = "serverId", required = true) Long serverId,
            @RequestParam(value = "appId", required = true) Long appId
    ) {

        if (coreServerService.appDelete(serverId, appId)) {
            return R.ok("delete app successfully!");
        }

        return R.failed("delete app unsuccessful");

    }

    // modify -- just modify the appInfo is ok




}
