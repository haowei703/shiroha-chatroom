package com.shiroha.chatroom.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Accessors(chain = true)
@TableName(value = "group_member", schema = "public")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @MppMultiId("group_id")
    private Long groupId;

    @MppMultiId("member_id")
    private UUID memberId;

    // 群内昵称
    private String nickname;

    // 入群时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDate joinedAt;

    @TableField("role")
    private String role;

    private int status;
}
