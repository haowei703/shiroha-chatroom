package com.shiroha.chatroom.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@TableName(value = "friend_requests", schema = "public")
public class FriendRequestDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private UUID userId;

    private UUID friendId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDate createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDate updatedAt;
}
