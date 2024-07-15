package com.shiroha.chatroom.types;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 消息状态枚举类
 */
@Getter
@AllArgsConstructor
public enum MessageStatus {
    // 发送中
    SENDING(0),
    // 已发送
    SENT(1),
    // 已读
    READ(2),
    // 未读
    UNREAD(3),
    // 删除
    DELETED(4),
    // 发送失败
    FAILED(5),
    // 撤回
    RECALLED(6);

    @EnumValue
    private final Integer value;

    public static MessageStatus getMessageStatus(Integer value){
        for(MessageStatus ms : MessageStatus.values()){
            if(Objects.equals(ms.value, value)){
                return ms;
            }
        }
        throw new IllegalArgumentException("No MessageStatus with value " + value);
    }
}

