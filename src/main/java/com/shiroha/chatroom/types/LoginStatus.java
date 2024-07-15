package com.shiroha.chatroom.types;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录状态枚举类
 */
@Getter
@AllArgsConstructor
public enum LoginStatus {
    ONLINE(1),
    OFFLINE(2),
    STEALTH(3);

    @EnumValue
    private final Integer value;

    public static LoginStatus fromValue(Integer value) {
        for (LoginStatus loginStatus : LoginStatus.values()) {
            if (loginStatus.value.equals(value)) {
                return loginStatus;
            }
        }
        throw new IllegalArgumentException("No LoginStatus with value " + value);
    }
}
