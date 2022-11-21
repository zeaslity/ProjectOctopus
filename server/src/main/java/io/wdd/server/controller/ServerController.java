package io.wdd.server.controller;


import io.wdd.wddcommon.utils.R;
import io.wdd.server.beans.po.ServerInfoPO;
import io.wdd.server.beans.vo.ServerInfoVO;
import io.wdd.server.coreService.CoreServerService;
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
    public R<List> getAllServerInfo() {

        return R.ok(coreServerService.getServerInfoList());
    }

    @GetMapping("/allIncludeDelete")
    public R<List> getAllServerInfoIncludeDelete() {

        return R.ok(coreServerService.getServerInfoListIncludeDelete());
    }

    @PostMapping("/single")
    public R getSingleServerInfo(
            @RequestParam(value = "serverIPv4") @Nullable String ipv4,
            @RequestParam(value = "serverName") @Nullable String serverName,
            @RequestParam(value = "serverLocation") @Nullable String serverLocation
    ) {
        return R.ok(coreServerService.getServerInfoSingle(serverName, ipv4, serverLocation));
    }

    @PostMapping("/newServer")
    public R createServerInfo(@RequestBody @Validated ServerInfoVO serverInfoVO) {

        if (coreServerService.createServerInfo(serverInfoVO)) {
            return R.ok("Create Server Success !");
        }

        return R.failed("Create Server Failed !");
    }

    @PostMapping("/updateServerInfo")
    public R updateServerInfo(@RequestBody ServerInfoPO serverInfoPO) {

        if (coreServerService.updateServerInfo(serverInfoPO)) {
            return R.ok("Server info update successfully !");
        }

        return R.failed("Server info update failed !");
    }

    @PostMapping("/deleteServer")
    public R deleteServer(
            @RequestParam(value = "serverId") @Nullable Long serverId,
            @RequestParam(value = "serverName") @Nullable String serverName) {

        if (coreServerService.deleteServer(serverId, serverName)) {
            R.ok("Delete Server Successfully !");
        }

        return R.failed("Delete Server Failed !");
    }

}
