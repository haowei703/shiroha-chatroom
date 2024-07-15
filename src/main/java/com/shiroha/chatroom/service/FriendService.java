package com.shiroha.chatroom.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.shiroha.chatroom.domain.FriendDO;
import com.shiroha.chatroom.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface FriendService extends IMppService<FriendDO> {

    /**
     * 发送好友申请
     * @param from 发起好友请求的userId
     * @param to 被邀请的userId
     */
    void sendFriendRequest(UUID from, UUID to);

    /**
     * 接收好友申请
     */
    void acceptFriendRequest(UUID from, UUID to);

    /**
     * 添加好友
     * @param from 发起好友请求的userId
     * @param to 被邀请的userId
     */
    void addFriend(UUID from, UUID to) throws Exception;

    /*
     * 删除好友
     * @param from 发起删除的userId
     * @param to 希望删除的userId
     */
    void removeFriend(UUID from, UUID to) throws Exception;

    /**
     * 获取好友列表
     * @param from uid
     * @return 好友列表
     */
    List<UserDTO> getFriends(UUID from);

    // 判断两个用户是否是好友
    boolean isFriend(UUID userId1, UUID userId2);
}
