package com.shiroha.chatroom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long Id;
    private String name;
    private UUID ownerId;
    private List<GroupMemberDTO> members;
}
