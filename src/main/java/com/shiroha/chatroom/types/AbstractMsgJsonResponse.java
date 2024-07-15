package com.shiroha.chatroom.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class AbstractMsgJsonResponse<T extends MsgJsonResponseContent<?>> implements MsgJsonResponse<T> {

    @Serial
    private static final long serialVersionUID=1L;

    private Long id;
    private String senderId;
    private String receiverId;
    private String groupId;

    @JsonProperty("type")
    private MessageType messageType;

    private T content;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    private String status;
}
