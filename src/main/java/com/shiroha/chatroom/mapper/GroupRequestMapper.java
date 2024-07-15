package com.shiroha.chatroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiroha.chatroom.domain.GroupJoinRequestDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupRequestMapper extends BaseMapper<GroupJoinRequestDO> {
}
