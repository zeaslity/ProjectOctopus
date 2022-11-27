package io.wdd.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.wdd.server.beans.po.AppdomainInfoPO;
import io.wdd.server.service.AppdomainInfoService;
import io.wdd.server.mapper.AppdomainInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author wdd
* @description 针对表【appdomain_info】的数据库操作Service实现
* @createDate 2022-11-27 16:08:43
*/
@Service
public class AppdomainInfoServiceImpl extends ServiceImpl<AppdomainInfoMapper, AppdomainInfoPO>
    implements AppdomainInfoService{

}




