package com.shiroha.chatroom.types;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.shiroha.chatroom.domain.ChatMessageDO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.nio.ByteBuffer;

/**
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("binary")
public final class BinaryChatMessage extends AbstractChatMessage<ByteBuffer>{

    private FileMetaData fileMetaData;

    @Override
    public MessageType getMessageType() {
        return this.messageType;
    }

    @Override
    public ChatMessageDO toChatMessageDO() {
        return new ChatMessageDO()
                .setSenderId(this.getSenderId())
                .setReceiverId(this.getReceiverId())
                .setGroupId(this.getGroupId())
                .setMessageType(this.getMessageType())
                .setCreatedAt(this.getTimestamp());
    }
}
