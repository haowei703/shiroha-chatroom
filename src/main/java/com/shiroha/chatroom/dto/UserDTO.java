package com.shiroha.chatroom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shiroha.chatroom.types.LoginStatus;
import com.shiroha.chatroom.types.TokenPair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    // 用户id
    private UUID id;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 邮箱
    private String email;
    // 头像URL
    private String avatar;
    // 在线状态
    private LoginStatus status;
    // token对
    @JsonProperty(value = "token")
    private TokenPair tokenPair;
}
