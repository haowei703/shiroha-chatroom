package com.shiroha.chatroom.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.nio.ByteBuffer;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class AudioMsgJsonResponseContent extends MsgJsonResponseContent<ByteBuffer> {
    private String encoding;
}
