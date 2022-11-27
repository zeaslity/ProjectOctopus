package io.wdd.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.wdd.server.beans.po.DomainInfoPO;
import io.wdd.server.service.DomainInfoService;
import io.wdd.server.mapper.DomainInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author wdd
* @description 针对表【domain_info】的数据库操作Service实现
* @createDate 2022-11-27 16:34:43
*/
@Service
public class DomainInfoServiceImpl extends ServiceImpl<DomainInfoMapper, DomainInfoPO>
    implements DomainInfoService{

}




