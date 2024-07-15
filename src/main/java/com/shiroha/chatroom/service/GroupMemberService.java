package com.shiroha.chatroom.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.shiroha.chatroom.domain.GroupMemberDO;

import java.util.UUID;

public interface GroupMemberService extends IMppService<GroupMemberDO> {
    /**
     * 判断用户是否是群成员
     * @param userId 用户id
     * @param groupId 群id
     */
    boolean isUserMemberOfGroup(UUID userId, Long groupId);

    /**
     * 是否是群管理员
     */
    boolean isAdmin(UUID userId, Long groupId);

    /**
     * 发送入群申请
     */
    void sendGroupJoinRequest(UUID userId, Long groupId);

    /**
     * 同意入群申请
     */
    void acceptGroupJoinRequest(UUID userId, Long groupId);
}
