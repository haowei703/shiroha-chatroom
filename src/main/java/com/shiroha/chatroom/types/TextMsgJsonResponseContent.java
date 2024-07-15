package com.shiroha.chatroom.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class TextMsgJsonResponseContent extends MsgJsonResponseContent<String> {
}
