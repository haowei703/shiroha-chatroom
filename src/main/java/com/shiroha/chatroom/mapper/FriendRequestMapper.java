package com.shiroha.chatroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiroha.chatroom.domain.FriendRequestDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FriendRequestMapper extends BaseMapper<FriendRequestDO> {
}
