package com.shiroha.chatroom.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public final class FileMsgJsonResponseContent extends MsgJsonResponseContent<String> {
    // 文件名
    private String fileName;
    // 文件下载地址
    private String url;
}
