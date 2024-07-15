package com.shiroha.chatroom.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@TableName(value = "group_join_requests", schema = "public")
public class GroupJoinRequestDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private UUID userId;

    private Long groupId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDate createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDate updatedAt;
}
