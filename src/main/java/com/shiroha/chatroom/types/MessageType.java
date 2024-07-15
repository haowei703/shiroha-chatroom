package com.shiroha.chatroom.types;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 消息类型枚举类
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    TEXT("text"),
    IMAGE("image"),
    AUDIO("audio"),
    FILE("file");

    @EnumValue
    @JsonValue
    private final String value;

    public static MessageType getMessageType(String value){
        for(MessageType mt : MessageType.values()){
            if(Objects.equals(mt.value, value)){
                return mt;
            }
        }
        throw new IllegalArgumentException("No MessageType with value " + value);
    }
}
