package com.shiroha.chatroom.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shiroha.chatroom.dto.GroupMemberDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long Id;
    private String name;
    private String ownerId;
    private List<GroupMemberDTO> members;
}
