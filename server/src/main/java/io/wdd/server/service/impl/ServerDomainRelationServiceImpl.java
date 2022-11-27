package io.wdd.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.wdd.server.beans.po.ServerDomainRelationPO;
import io.wdd.server.service.ServerDomainRelationService;
import io.wdd.server.mapper.ServerDomainRelationMapper;
import org.springframework.stereotype.Service;

/**
* @author wdd
* @description 针对表【server_domain_relation】的数据库操作Service实现
* @createDate 2022-11-27 17:28:36
*/
@Service
public class ServerDomainRelationServiceImpl extends ServiceImpl<ServerDomainRelationMapper, ServerDomainRelationPO>
    implements ServerDomainRelationService{

}




