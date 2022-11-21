package io.wdd.server.service;

import io.wdd.server.beans.po.ServerInfoPO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wdd
* @description 针对表【server_info】的数据库操作Service
* @createDate 2022-11-20 16:16:52
*/
public interface ServerInfoService extends IService<ServerInfoPO> {

    /**
     * @return all servers include delete servers
     */
    List<ServerInfoPO> getAll();

}
