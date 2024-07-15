package com.shiroha.chatroom.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiroha.chatroom.domain.GroupDO;
import com.shiroha.chatroom.dto.GroupDTO;
import com.shiroha.chatroom.dto.GroupMemberDTO;
import com.shiroha.chatroom.dto.GroupsDTO;
import com.shiroha.chatroom.mapper.GroupMapper;
import com.shiroha.chatroom.producer.MemberQueueProducer;
import com.shiroha.chatroom.service.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    private final GroupMapper groupMapper;

    private final MemberQueueProducer producer;

    @Override
    @Transactional
    public void createGroup(GroupDTO groupDTO) throws RuntimeException {
        List<GroupMemberDTO> members = groupDTO.getMembers();
        if(members.isEmpty()) throw new RuntimeException("members is empty");

        GroupDO groupDO = new GroupDO()
                .setName(groupDTO.getName())
                .setOwnerId(groupDTO.getOwnerId());
        try {
            groupMapper.insert(groupDO);
            members.forEach(groupMemberDTO -> groupMemberDTO.setGroupId(groupDO.getId()));
            producer.sendMessage(groupDTO.getMembers());
        }catch (Exception e){
            log.error("create group error", e);
            throw new RuntimeException("create group error", e);
        }
    }

    @Override
    public GroupDTO getGroupById(Long groupId) {
        GroupDO groupDOS = groupMapper.searchByIdGroupDtoList(groupId);
        List<GroupMemberDTO> groupMemberDTOS = groupDOS.getMembers().stream().map(groupMemberDO -> new GroupMemberDTO()
                .setGroupId(groupId)
                .setMemberId(groupMemberDO.getMemberId())
                .setNickname(groupMemberDO.getNickname())
                .setJoinedAt(groupMemberDO.getJoinedAt())
                .setRole(groupMemberDO.getRole())
        ).toList();

        return GroupDTO.builder()
                .Id(groupDOS.getId())
                .name(groupDOS.getName())
                .ownerId(groupDOS.getOwnerId())
                .members(groupMemberDTOS)
                .build();
    }

    @Override
    public List<GroupsDTO> getAllGroupsByUserId(UUID userId) {
        return groupMapper.searchAllByUserIdGroupDtoList(userId);
    }
}
