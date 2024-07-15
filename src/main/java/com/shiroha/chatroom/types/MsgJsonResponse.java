package com.shiroha.chatroom.types;

import java.io.Serializable;

public interface MsgJsonResponse<T> extends Serializable {
    void setContent(T content);
    T getContent();
    void setMessageType(MessageType type);
    MessageType getMessageType();
}
