package com.shiroha.chatroom.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.shiroha.chatroom.domain.GroupMemberDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@CacheNamespace
public interface GroupMemberMapper extends MppBaseMapper<GroupMemberDO> {
}
