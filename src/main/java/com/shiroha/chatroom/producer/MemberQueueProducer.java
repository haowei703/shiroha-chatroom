package com.shiroha.chatroom.producer;

import com.shiroha.chatroom.dto.GroupMemberDTO;
import com.shiroha.chatroom.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberQueueProducer {

    private final AmqpTemplate amqpTemplate;

    public MemberQueueProducer(RabbitTemplate rabbitTemplate) {
        this.amqpTemplate = rabbitTemplate;
    }

    public void sendMessage(List<GroupMemberDTO> groupMemberDTOList) {
        String json = JsonUtils.serialize(groupMemberDTOList);
        amqpTemplate.convertAndSend("member-exchange", "member-queue", json);
    }
}
