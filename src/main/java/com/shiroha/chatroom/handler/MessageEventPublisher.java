package com.shiroha.chatroom.handler;

import com.shiroha.chatroom.types.BinaryChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MessageEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public MessageEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish(BinaryChatMessage message, String type) {
        MessageEvent messageEvent = new MessageEvent(this, message, type);
        eventPublisher.publishEvent(messageEvent);
    }
}
