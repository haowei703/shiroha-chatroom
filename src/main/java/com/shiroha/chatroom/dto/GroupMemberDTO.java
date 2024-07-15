package com.shiroha.chatroom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class GroupMemberDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long groupId;
    private UUID memberId;
    private String nickname;
    private LocalDate joinedAt;
    private String role;
}
