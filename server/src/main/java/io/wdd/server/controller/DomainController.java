package io.wdd.server.controller;

import io.wdd.server.beans.po.DomainInfoPO;
import io.wdd.server.beans.vo.DomainInfoVO;
import io.wdd.server.coreService.CoreDomainService;
import io.wdd.common.beans.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;

/**
 * todo call this api to automatically get all dns record from cloudflare
 */
@RestController
@RequestMapping("/domain")
public class DomainController {

    @Resource
    CoreDomainService coreDomainService;

    // read
    @GetMapping("getAll")
    public R<List<DomainInfoVO>> getAll() {
        return R.ok(coreDomainService.getAll());
    }

    @GetMapping("getSingle")
    public R<List<DomainInfoVO>> getSingle(@RequestParam(value = "domainName", required = false) @Nullable String domainName, @RequestParam(value = "dnsIP", required = false) @Nullable String dnsIP

    ) {

        return R.ok(coreDomainService.getSingle(domainName, dnsIP));
    }

    // create
    @PostMapping("create")
    public R<String> create(@RequestBody @Validated DomainInfoVO domainInfoVO) {

        if (coreDomainService.create(domainInfoVO)) {
            return R.ok("create domain successfully !");
        }


        return R.failed("create domain failed !");
    }


    // update
    @PostMapping("update")
    public R<String> update(@RequestBody @Validated DomainInfoPO domainInfoPO) {

        if (coreDomainService.update(domainInfoPO)) {
            return R.ok("update domain successfully !");
        }

        return R.failed("update domain failed !");
    }

    // delete
    @PostMapping("delete")
    public R<String> delete(
            @RequestParam(value = "domainId") Long domainId
    ) {

        if (coreDomainService.delete(domainId)) {
            return R.ok("delete domain successfully !");
        }

        return R.failed("delete domain failed !");
    }

}
