package com.shiroha.chatroom.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.shiroha.chatroom.domain.GroupJoinRequestDO;
import com.shiroha.chatroom.domain.GroupMemberDO;
import com.shiroha.chatroom.mapper.GroupMemberMapper;
import com.shiroha.chatroom.mapper.GroupRequestMapper;
import com.shiroha.chatroom.service.GroupMemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class GroupMemberServiceImpl extends MppServiceImpl<GroupMemberMapper, GroupMemberDO> implements GroupMemberService {

    private final GroupMemberMapper groupMemberMapper;

    private final GroupRequestMapper groupRequestMapper;

    @Override
    public boolean isUserMemberOfGroup(UUID userId, Long groupId) {
        LambdaQueryWrapper<GroupMemberDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupMemberDO::getGroupId, groupId)
                .eq(GroupMemberDO::getMemberId, userId);
        return groupMemberMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public void sendGroupJoinRequest(UUID userId, Long groupId) throws RuntimeException {
        try {
            GroupJoinRequestDO groupJoinRequestDO = new GroupJoinRequestDO();
            groupJoinRequestDO.setGroupId(groupId);
            groupJoinRequestDO.setUserId(userId);
            groupRequestMapper.insert(groupJoinRequestDO);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void acceptGroupJoinRequest(UUID userId, Long groupId) {
        try {
            LambdaQueryWrapper<GroupMemberDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GroupMemberDO::getGroupId, groupId)
                    .eq(GroupMemberDO::getMemberId, userId);
            groupRequestMapper.deleteById(queryWrapper);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Override
    public boolean isAdmin(UUID userId, Long groupId) {
        LambdaQueryWrapper<GroupMemberDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupMemberDO::getGroupId, groupId)
                .eq(GroupMemberDO::getMemberId, userId);
        GroupMemberDO groupMemberDO = groupMemberMapper.selectOne(queryWrapper);
        return Objects.equals(groupMemberDO.getRole(), "ADMIN");
    }
}
