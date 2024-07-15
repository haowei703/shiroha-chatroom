package com.shiroha.chatroom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiroha.chatroom.domain.GroupDO;
import com.shiroha.chatroom.dto.GroupDTO;
import com.shiroha.chatroom.dto.GroupsDTO;

import java.util.List;
import java.util.UUID;

public interface GroupService extends IService<GroupDO> {
    /**
     * 创建群聊
     */
    void createGroup(GroupDTO groupDTO) throws Exception;

    /**
     * 查询指定群组
     * @param groupId 组id
     * @return 查询结果
     */
    GroupDTO getGroupById(Long groupId);

    /**
     * 获取某个用户加入的所有群组
     * @param userId 获取某个用户加入的全部群聊
     * @return 群组列表
     */
    List<GroupsDTO> getAllGroupsByUserId(UUID userId);
}
