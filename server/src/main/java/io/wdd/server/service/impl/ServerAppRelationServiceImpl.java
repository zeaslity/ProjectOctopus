package io.wdd.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.wdd.server.beans.po.ServerAppRelationPO;
import io.wdd.server.service.ServerAppRelationService;
import io.wdd.server.mapper.ServerAppRelationMapper;
import org.springframework.stereotype.Service;

/**
* @author wdd
* @description 针对表【server_app_relation】的数据库操作Service实现
* @createDate 2022-11-27 13:53:22
*/
@Service
public class ServerAppRelationServiceImpl extends ServiceImpl<ServerAppRelationMapper, ServerAppRelationPO>
    implements ServerAppRelationService{

}




