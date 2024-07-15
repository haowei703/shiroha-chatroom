package com.shiroha.chatroom.controller;

import com.shiroha.chatroom.domain.GroupMemberDO;
import com.shiroha.chatroom.domain.UserDO;
import com.shiroha.chatroom.dto.GroupDTO;
import com.shiroha.chatroom.dto.GroupsDTO;
import com.shiroha.chatroom.service.GroupMemberService;
import com.shiroha.chatroom.service.GroupService;
import com.shiroha.chatroom.utils.Result;
import com.shiroha.chatroom.vo.GroupVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    private final GroupMemberService groupMemberService;

    @GetMapping
    public ResponseEntity<Result> getGroupById(@RequestParam("group_id") Long groupId) {
        GroupDTO groupDTO = groupService.getGroupById(groupId);
        if(groupDTO != null) {
            return ResponseEntity.ok(Result.ok().setData(groupDTO));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<GroupsDTO>> getGroupsByUserId(@AuthenticationPrincipal UserDO user) {
        try {
            List<GroupsDTO> groupsDTOS = groupService.getAllGroupsByUserId(user.getId());
            if(groupsDTOS != null) {
                return ResponseEntity.ok(groupsDTOS);
            }
            return ResponseEntity.notFound().build();
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Result> createGroup(@RequestBody GroupVO group, @AuthenticationPrincipal UserDO user) {
        if(!Objects.equals(group.getOwnerId(), user.getId().toString())) {
            return ResponseEntity.status(403).build();
        }

        try {
            GroupDTO groupDTO = GroupDTO.builder()
                    .name(group.getName())
                    .ownerId(UUID.fromString(group.getOwnerId()))
                    .members(group.getMembers())
                    .build();
            groupService.createGroup(groupDTO);
            return ResponseEntity.ok(Result.ok("group created"));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/request/send")
    public ResponseEntity<Result> sendGroupJoinRequest(@RequestParam("group_id") String groupId, @AuthenticationPrincipal UserDO user) {
        if(groupMemberService.isUserMemberOfGroup(user.getId(), Long.valueOf(groupId))) {
            return ResponseEntity.status(403).build();
        }

        try {
            groupMemberService.sendGroupJoinRequest(user.getId(), Long.valueOf(groupId));
            return ResponseEntity.ok(Result.ok("group request sent"));
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/request/accept")
    public ResponseEntity<Result> acceptGroupJoinRequest(@RequestParam("user_id") String userId, @RequestParam("group_id") String groupId, @AuthenticationPrincipal UserDO user) {
        if(groupMemberService.isAdmin(user.getId(), Long.valueOf(groupId)) && !groupMemberService.isUserMemberOfGroup(UUID.fromString(userId), Long.valueOf(groupId))) {
            groupMemberService.acceptGroupJoinRequest(UUID.fromString(userId), Long.valueOf(groupId));
            groupMemberService.save(new GroupMemberDO()
                    .setGroupId(Long.valueOf(groupId))
                    .setMemberId(UUID.fromString(userId)));
            return ResponseEntity.ok(Result.ok("group accepted"));
        }else return ResponseEntity.status(401).build();
    }
}
