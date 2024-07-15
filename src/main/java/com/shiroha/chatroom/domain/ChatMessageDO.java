package com.shiroha.chatroom.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.shiroha.chatroom.types.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 聊天记录
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@TableName(value = "chat_message", schema = "public")
public class ChatMessageDO implements Serializable {

    @Serial
    private static final long serialVersionUID=1L;

    /**
     * 消息id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送者id
     */
    private UUID senderId;

    /**
     * 接收者id
     */
    private UUID receiverId;

    /**
     * 组id，为空表示该消息为私聊
     */
    private Long groupId;

    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息发送时间
     */
    private LocalDateTime createdAt;

    /**
     * 消息状态
     */
    private String status;

    /**
     * 拓展字段，用于存储音频、图像等二进制数据
     */
    private byte[] data;
}