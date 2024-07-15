package com.shiroha.chatroom.handler;

import com.shiroha.chatroom.types.BinaryChatMessage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 消息事件
 */
@Getter
@Setter
public class MessageEvent extends ApplicationEvent {

    private BinaryChatMessage chatMessage;

    private String type;

    public MessageEvent(Object source, BinaryChatMessage chatMessage, String type) {
        super(source);
        this.chatMessage = chatMessage;
        this.type = type;
    }
}
