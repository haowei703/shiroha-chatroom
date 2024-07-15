package com.shiroha.chatroom.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.experimental.SuperBuilder;

/**
 * 文件消息响应类
 */
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileMsgJsonResponse extends AbstractMsgJsonResponse<FileMsgJsonResponseContent> {
}
