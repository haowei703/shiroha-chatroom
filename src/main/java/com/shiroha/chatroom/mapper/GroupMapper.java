package com.shiroha.chatroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiroha.chatroom.domain.GroupDO;
import com.shiroha.chatroom.dto.GroupsDTO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Mapper
@Repository
@CacheNamespace
public interface GroupMapper extends BaseMapper<GroupDO> {
    void insertGroup(GroupDO groupDO);
    GroupDO searchByIdGroupDtoList(@Param("groupId") Long groupId);
    List<GroupsDTO> searchAllByUserIdGroupDtoList(@Param("userId") UUID userId);
}
