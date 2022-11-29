package io.wdd.server.coreService.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import io.wdd.server.beans.po.DomainInfoPO;
import io.wdd.server.beans.vo.DomainInfoVO;
import io.wdd.server.coreService.CoreDomainService;
import io.wdd.common.handler.MyRuntimeException;
import io.wdd.server.service.DomainInfoService;
import io.wdd.server.utils.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CoreDomainServiceImpl implements CoreDomainService {

    @Resource
    DomainInfoService domainInfoService;


    @Override
    public List<DomainInfoVO> getAll() {
        return EntityUtils.cvToTarget(domainInfoService.list(), DomainInfoVO.class);
    }

    @Override
    public List<DomainInfoVO> getSingle(String domainName, String dnsIP) {

        if (null == domainName && null == dnsIP) {
            throw new MyRuntimeException("query params are wrong !");
        }

        List<DomainInfoPO> domainInfoPOList = new LambdaQueryChainWrapper<DomainInfoPO>(domainInfoService.getBaseMapper())
                .like(StringUtils.isNotEmpty(domainName), DomainInfoPO::getDomainName, domainName)
                .eq(StringUtils.isNotEmpty(dnsIP), DomainInfoPO::getDnsIp, dnsIP)
                .list();

        Assert.notEmpty(domainInfoPOList,"no such domain !");


        return EntityUtils.cvToTarget(domainInfoPOList, DomainInfoVO.class);
    }

    @Override
    public boolean create(DomainInfoVO domainInfoVO) {

        return domainInfoService.save(EntityUtils.cvToTarget(domainInfoVO, DomainInfoPO.class));
    }

    @Override
    public boolean update(DomainInfoPO domainInfoPO) {
        Assert.notNull(domainInfoService.getById(domainInfoPO.getDomainId()), "update domain info failed ! can't find original one !");


        return domainInfoService.updateById(domainInfoPO);
    }

    @Override
    public boolean delete(Long domainId) {
        Assert.notNull(domainInfoService.getById(domainId), "delete domain info failed ! can't find original one !");

        return domainInfoService.removeById(domainId);
    }
}
