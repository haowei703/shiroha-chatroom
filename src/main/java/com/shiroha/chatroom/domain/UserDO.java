package com.shiroha.chatroom.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shiroha.chatroom.types.LoginStatus;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
@TableName(value = "user_entity", schema = "public")
public class UserDO implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private UUID id;

    @TableField
    private String username;

    private String password;

    private String email;

    private String avatar;

    private String role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDate createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDate updatedAt;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime lastLoginTime;

    private LoginStatus status;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override//返回true，代表用户账号没过期
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override//返回true，代表用户账号没被锁定
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override//返回true，代表用户密码没有过期
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override//返回true，代表用户账号还能够使用
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
