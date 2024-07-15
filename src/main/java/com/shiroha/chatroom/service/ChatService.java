package com.shiroha.chatroom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiroha.chatroom.domain.ChatMessageDO;
import com.shiroha.chatroom.dto.ChatMessageDTO;
import com.shiroha.chatroom.types.ChatMessage;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ChatService extends IService<ChatMessageDO> {
    /**
     * 保存消息
     * @param message 消息
     * @return true:保存成功;false:保存失败
     */
    boolean saveMessage(ChatMessage<?> message);

    /**
     * 分页查询聊天记录
     * @return 分页查询结果
     */
    ChatMessageDTO getMessageByPage(ChatMessageDTO chatMessageDTO);

    /**
     * 获取上传的文件名
     */
    String getFilename(UUID senderId, UUID receiverId, LocalDateTime timestamp);
}
