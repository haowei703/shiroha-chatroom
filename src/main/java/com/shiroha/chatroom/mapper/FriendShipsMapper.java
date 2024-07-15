package com.shiroha.chatroom.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.shiroha.chatroom.domain.FriendDO;
import com.shiroha.chatroom.dto.UserDTO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Mapper
@Repository
@CacheNamespace
public interface FriendShipsMapper extends MppBaseMapper<FriendDO> {

    List<UserDTO> getFriends(@Param("from") UUID from);

    boolean addFriend(@Param("from") UUID from, @Param("to") UUID to);

    boolean removeFriend(@Param("from") UUID from, @Param("to") UUID to);
}
