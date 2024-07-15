package com.shiroha.chatroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shiroha.chatroom.domain.ChatMessageDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Mapper
@Repository
@CacheNamespace
public interface ChatMapper extends BaseMapper<ChatMessageDO> {

    /**
     * 分页查询私聊记录
     * @param page 分页
     * @param userId1 双方其一
     * @param userId2 双方其二
     * @return 分页查询结果
     */
    IPage<ChatMessageDO> searchPrivateChatMessageByPage(IPage<ChatMessageDO> page, @Param("senderId") UUID userId1, @Param("receiverId") UUID userId2);

    /**
     * 分页查询群聊记录
     * @param page 分页
     * @param senderId 消息发送者
     * @param groupId 群聊
     * @return 分页查询结果
     */
    IPage<ChatMessageDO> searchGroupChatMessageByPage(IPage<ChatMessageDO> page, @Param("senderId") UUID senderId, @Param("groupId") Long groupId);
}
