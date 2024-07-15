package com.shiroha.chatroom.types;

import com.shiroha.chatroom.domain.ChatMessageDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"senderId", "receiverId", "timestamp"})
public abstract class AbstractChatMessage<T> implements Comparable<AbstractChatMessage<T>>, ChatMessage<T> {

    // 发送者id
    public UUID senderId;
    // 接收者id
    public UUID receiverId;
    // 群组id
    public Long groupId;
    // 发送时间
    public LocalDateTime timestamp;
    // 消息类型
    public MessageType messageType;
    // 消息内容
    public T data;

    public abstract MessageType getMessageType();

    @Override
    public int compareTo(AbstractChatMessage<T> other) {
        return this.timestamp.compareTo(other.timestamp);
    }
}
