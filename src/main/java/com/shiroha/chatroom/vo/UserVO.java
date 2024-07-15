package com.shiroha.chatroom.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shiroha.chatroom.types.LoginStatus;
import com.shiroha.chatroom.types.TokenPair;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class UserVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    // 用户id
    private UUID id;
    // 用户名
    private String username;
    // 密码
    @Size(min = 6, max = 16)
    private String password;
    // 邮箱
    @Email
    private String email;
    // 头像URI
    private String avatar;
    // 在线状态
    private LoginStatus status;
    // token对
    @JsonProperty(value = "token")
    private TokenPair tokenPair;
}
