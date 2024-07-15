package com.shiroha.chatroom.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.jeffreyning.mybatisplus.service.MppServiceImpl;
import com.shiroha.chatroom.domain.FriendDO;
import com.shiroha.chatroom.domain.FriendRequestDO;
import com.shiroha.chatroom.dto.UserDTO;
import com.shiroha.chatroom.mapper.FriendRequestMapper;
import com.shiroha.chatroom.mapper.FriendShipsMapper;
import com.shiroha.chatroom.service.FriendService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class FriendServiceImpl extends MppServiceImpl<FriendShipsMapper, FriendDO> implements FriendService {

    private final FriendShipsMapper friendShipsMapper;

    private final FriendRequestMapper friendRequestMapper;

    @Override
    @Transactional
    public void sendFriendRequest(UUID from, UUID to) {
        try {
            FriendRequestDO friendRequestDO = new FriendRequestDO();
            friendRequestDO.setUserId(from);
            friendRequestDO.setFriendId(to);
            friendRequestMapper.insert(friendRequestDO);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void acceptFriendRequest(UUID from, UUID to) {
        LambdaQueryWrapper<FriendRequestDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FriendRequestDO::getUserId, from)
                .eq(FriendRequestDO::getFriendId, to);
        friendRequestMapper.delete(queryWrapper);
    }

    @Override
    public void addFriend(UUID from, UUID to) throws RuntimeException {
        try {
            FriendDO friendships = FriendDO.builder()
                    .userId(from)
                    .friendId(to)
                    .build();
            friendShipsMapper.insert(friendships);
        }catch (Exception e){
            log.error("addFriend error", e);
            throw new RuntimeException("addFriend error", e);
        }
    }

    @Override
    @Transactional
    public void removeFriend(UUID from, UUID to) {
    }

    @Override
    public List<UserDTO> getFriends(UUID from) {
        try {
            return friendShipsMapper.getFriends(from);
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isFriend(UUID userId1, UUID userId2) {
        LambdaQueryWrapper<FriendDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .and(qw -> qw
                        .eq(FriendDO::getUserId, userId1)
                        .eq(FriendDO::getFriendId, userId2))
                .or(qw -> qw
                        .eq(FriendDO::getUserId, userId2)
                        .eq(FriendDO::getFriendId, userId1));
        return friendShipsMapper.selectCount(queryWrapper) > 0;
    }
}
