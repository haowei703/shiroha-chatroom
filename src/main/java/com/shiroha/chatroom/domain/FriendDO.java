package com.shiroha.chatroom.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 *
 */
@Data
@Builder
@TableName(value = "friend", schema = "public")
public class FriendDO implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    @MppMultiId
    @TableField("uid")
    private UUID userId;

    @MppMultiId
    @TableField("f_uid")
    private UUID friendId;
}
