package com.shiroha.chatroom.types;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 群内角色
 */
@Getter
@AllArgsConstructor
public enum GroupRole {
    USER("user"),
    ADMIN("admin"),
    OWNER("owner");

    @EnumValue
    @JsonValue
    private final String value;

    public static GroupRole fromValue(String value) {
        for (GroupRole groupRole : GroupRole.values()) {
            if (groupRole.value.equals(value)) {
                return groupRole;
            }
        }
        throw new IllegalArgumentException("No GroupRole with value " + value);
    }
}
