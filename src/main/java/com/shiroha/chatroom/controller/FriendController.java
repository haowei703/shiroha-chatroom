package com.shiroha.chatroom.controller;

import com.shiroha.chatroom.domain.UserDO;
import com.shiroha.chatroom.dto.UserDTO;
import com.shiroha.chatroom.service.FriendService;
import com.shiroha.chatroom.utils.ConversionUtils;
import com.shiroha.chatroom.utils.Result;
import com.shiroha.chatroom.vo.UserVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/friends")
@AllArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<Result> getFriends(@AuthenticationPrincipal UserDO user) {
        try {
            List<UserDTO> userDTOS = friendService.getFriends(user.getId());
            if (userDTOS.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            List<UserVO> userVOS = ConversionUtils.convertList(userDTOS, UserVO.class);
            return ResponseEntity.ok().body(Result.ok().setData(userVOS));
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/request/send")
    public ResponseEntity<Result> sendFriendRequest(@RequestParam("user_id") String userId, @AuthenticationPrincipal UserDO user) {
        try {
            friendService.sendFriendRequest(user.getId(), UUID.fromString(userId));
            return ResponseEntity.ok().body(Result.ok("request sent"));
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/request/accept")
    public ResponseEntity<Result> acceptFriend(@RequestParam("user_id") String userId, @AuthenticationPrincipal UserDO user) {
        try {
            friendService.acceptFriendRequest(UUID.fromString(userId), user.getId());
            friendService.addFriend(user.getId(), UUID.fromString(userId));
            return ResponseEntity.ok().body(Result.ok("add success"));
        }catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
