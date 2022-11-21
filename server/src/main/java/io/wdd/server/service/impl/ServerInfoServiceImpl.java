package io.wdd.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.wdd.server.beans.po.ServerInfoPO;
import io.wdd.server.service.ServerInfoService;
import io.wdd.server.mapper.ServerInfoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author wdd
* @description 针对表【server_info】的数据库操作Service实现
* @createDate 2022-11-20 16:16:52
*/
@Service
public class ServerInfoServiceImpl extends ServiceImpl<ServerInfoMapper, ServerInfoPO>
    implements ServerInfoService{

    @Override
    public List<ServerInfoPO> getAll() {

        return this.baseMapper.getAll();
    }
}




