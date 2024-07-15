package com.shiroha.chatroom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shiroha.chatroom.types.MsgJsonResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageDTO {
    private UUID senderId;
    private UUID receiverId;
    private Long groupId;
    private long pageNum;
    private long pageSize;
    private long totalPages;
    private long totalCounts;
    private List<MsgJsonResponse<?>> msgJsonResponseList;
}
