package io.wdd.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.wdd.server.beans.po.AppInfoPO;
import io.wdd.server.service.AppInfoService;
import io.wdd.server.mapper.AppInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author wdd
* @description 针对表【app_info】的数据库操作Service实现
* @createDate 2022-11-20 16:49:20
*/
@Service
public class AppInfoServiceImpl extends ServiceImpl<AppInfoMapper, AppInfoPO>
    implements AppInfoService{

}




