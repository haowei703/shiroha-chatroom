package com.shiroha.chatroom.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.nio.ByteBuffer;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class ImageMsgJsonResponseContent extends MsgJsonResponseContent<ByteBuffer> {
    // 图片宽度
    private long width;
    // 图片高度
    private long height;
}
