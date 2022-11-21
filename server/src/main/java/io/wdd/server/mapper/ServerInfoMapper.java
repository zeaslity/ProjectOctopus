package io.wdd.server.mapper;

import io.wdd.server.beans.po.ServerInfoPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author wdd
* @description 针对表【server_info】的数据库操作Mapper
* @createDate 2022-11-20 16:16:52
* @Entity io.wdd.server.beans.po.ServerInfoPO
*/
public interface ServerInfoMapper extends BaseMapper<ServerInfoPO> {

    List<ServerInfoPO> getAll();
}




