package com.shiroha.chatroom.consumer;

import com.rabbitmq.client.Channel;
import com.shiroha.chatroom.service.ChatService;
import com.shiroha.chatroom.types.ChatMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * 聊天消息消费者
 */
@Component
public class ChatQueueConsumer {

    private final ChatService chatService;

    @Autowired
    public ChatQueueConsumer(ChatService chatService) {
        this.chatService = chatService;
    }

    @Transactional
    @RabbitListener(queues = "chat-queue")
    public void listen(ChatMessage<?> message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        try {
            boolean success = chatService.saveMessage(message);
            if(success) {
                channel.basicAck(deliveryTag, false);
            }else {
                channel.basicNack(deliveryTag, false, true);
            }
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, true);
            throw new RuntimeException(e);
        }
    }
}
