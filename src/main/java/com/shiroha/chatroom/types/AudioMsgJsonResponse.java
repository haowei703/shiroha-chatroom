package com.shiroha.chatroom.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.experimental.SuperBuilder;

/**
 * 音频消息响应类
 */
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AudioMsgJsonResponse extends AbstractMsgJsonResponse<AudioMsgJsonResponseContent> {
}
