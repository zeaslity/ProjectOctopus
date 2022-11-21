package io.wdd.server.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyBatisAutoInsertInterceptor implements MetaObjectHandler {


    /**
     * mybatis will inject and deal with some field defined down there
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("MyBaitsPlus start to insert manually !");

        this.strictInsertFill(metaObject, "registerTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "create_time", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
    }

    @Override
    public void updateFill(MetaObject metaObject) {

        log.info("MyBaitsPlus start to update manually !");

        this.strictInsertFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)

        this.strictInsertFill(metaObject, "update_time", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
    }
}
