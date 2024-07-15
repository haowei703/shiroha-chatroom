package com.shiroha.chatroom.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@TableName(value = "group", schema = "public")
public class GroupDO implements Serializable {

    @Serial
    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    // 群组id
    private Long Id;
    // 群名
    private String name;
    // 群主id
    private UUID ownerId;
    // 群组状态
    private int status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDate createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDate updatedAt;

    @TableField(exist = false)
    private List<GroupMemberDO> members;
}
