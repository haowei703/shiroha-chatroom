package com.shiroha.chatroom.producer;

import com.shiroha.chatroom.types.ChatMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 聊天消息生产者
 */
@Component
public class ChatQueueProducer {

    private final AmqpTemplate amqpTemplate;

    public ChatQueueProducer(RabbitTemplate rabbitTemplate) {
        this.amqpTemplate = rabbitTemplate;
    }

    public void sendMessage(ChatMessage<?> message) {
        amqpTemplate.convertAndSend("chat-exchange", "chat-queue", message);
    }
}
