package com.shiroha.chatroom.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shiroha.chatroom.types.MsgJsonResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // 当前查询页面
    private long pageNum;
    // 页面大小
    private long pageSize;
    // 总页数
    private long totalPages;
    // 总数量
    private long totalCounts;
    // 查询结果
    @JsonProperty("messageList")
    private List<MsgJsonResponse<?>> msgJsonResponseList;
}
