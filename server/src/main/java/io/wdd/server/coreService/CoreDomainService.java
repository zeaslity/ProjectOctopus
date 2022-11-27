package io.wdd.server.coreService;

import io.wdd.server.beans.po.DomainInfoPO;
import io.wdd.server.beans.vo.DomainInfoVO;

import java.util.List;

public interface CoreDomainService {

    List<DomainInfoVO> getAll();

    List<DomainInfoVO> getSingle(String domainName, String dnsIP);

    boolean create(DomainInfoVO domainInfoVO);

    boolean update(DomainInfoPO domainInfoPO);

    boolean delete(Long domainId);

}
