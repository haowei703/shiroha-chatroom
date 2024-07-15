package com.shiroha.chatroom.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiroha.chatroom.domain.UserDO;
import com.shiroha.chatroom.dto.UserDTO;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public interface UserService extends IService<UserDO> {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return UserBO
     */
    UserDTO login(String username, String password) throws UsernameNotFoundException;

    /**
     * 用户注册
     * @param userDTO 注册信息
     * @return 注册成功后的用户信息
     */
    UserDTO register(UserDTO userDTO);

    /**
     * 通过用户名获取用户
     * @param username 用户名
     * @return 用户
     */
    UserDO getUserByUsername(String username);

    /**
     * 通过用户id获取用户
     */
    UserDO getUserByUserId(String userId);

    /**
     * 用户注销
     * @param userId 注销的用户id
     */
    void logout(UUID userId);

    /**
     * 获取用户信息
     * @param username 用户名
     */
    UserDTO getUserInfo(String username);
}
