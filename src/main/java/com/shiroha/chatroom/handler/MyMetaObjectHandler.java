package com.shiroha.chatroom.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * mybatis-plus自动填充策略
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDate::now, LocalDate.class);
        this.strictInsertFill(metaObject, "updatedAt", LocalDate::now, LocalDate.class);
        this.strictInsertFill(metaObject, "joinedAt", LocalDate::now, LocalDate.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDate::now, LocalDate.class);
    }
}
