package com.shiroha.chatroom.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import com.shiroha.chatroom.domain.GroupMemberDO;
import com.shiroha.chatroom.dto.GroupMemberDTO;
import com.shiroha.chatroom.service.GroupMemberService;
import com.shiroha.chatroom.utils.JsonUtils;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class MemberQueueConsumer {
    private final GroupMemberService memberService;

    @Autowired
    public MemberQueueConsumer(GroupMemberService memberService) {
        this.memberService = memberService;
    }

    @Transactional
    @RabbitListener(queues = "member-queue")
    public void listen(String json, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        try {
            List<GroupMemberDTO> groupMemberDTOS = JsonUtils.deserialize(json, new TypeReference<>() {});
            List<GroupMemberDO> groupMemberDOS = groupMemberDTOS.stream().map(groupMemberDTO -> new GroupMemberDO()
                    .setGroupId(groupMemberDTO.getGroupId())
                    .setMemberId(groupMemberDTO.getMemberId())
                    .setNickname(groupMemberDTO.getNickname())).toList();
            groupMemberDOS.forEach(this::safelyAddGroupMember);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, true);
            throw new RuntimeException(e);
        }
    }

    private void safelyAddGroupMember(GroupMemberDO groupMemberDO) {
        try {
            memberService.save(groupMemberDO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
