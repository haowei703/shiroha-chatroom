package com.shiroha.chatroom.types;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.shiroha.chatroom.domain.ChatMessageDO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("text")
public final class TextChatMessage extends AbstractChatMessage<String> {

    @Override
    public MessageType getMessageType() {
        return MessageType.TEXT;
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
