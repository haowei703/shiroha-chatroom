package com.shiroha.chatroom.types;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.shiroha.chatroom.domain.ChatMessageDO;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextChatMessage.class, name = "text"),
        @JsonSubTypes.Type(value = BinaryChatMessage.class, name = "binary")
})
public interface ChatMessage<T> {
    void setData(T data);
    T getData();
    MessageType getMessageType();
    ChatMessageDO toChatMessageDO();
}
