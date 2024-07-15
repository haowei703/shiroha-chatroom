package com.shiroha.chatroom.controller;

import com.shiroha.chatroom.domain.UserDO;
import com.shiroha.chatroom.dto.UserDTO;
import com.shiroha.chatroom.service.UserService;
import com.shiroha.chatroom.utils.ConversionUtils;
import com.shiroha.chatroom.utils.JwtUtils;
import com.shiroha.chatroom.utils.Result;
import com.shiroha.chatroom.vo.UserVO;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Result> login(@RequestBody UserVO userVO) {
        try {
            UserDTO user = userService.login(userVO.getUsername(), userVO.getPassword());
            userVO = ConversionUtils.convert(user, UserVO.class);
            return ResponseEntity.ok(Result.ok().setData(userVO));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("login failed"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Result> register(@RequestBody @Validated UserVO userVO) {
        try {
            UserDTO userDTO = ConversionUtils.convert(userVO, UserDTO.class);
            UserDTO user = userService.register(userDTO);
            userVO = ConversionUtils.convert(user, UserVO.class);
            return ResponseEntity.ok(Result.ok().setData(userVO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("user is already exist"));
        }
    }

    @GetMapping("/info/{username}")
    public ResponseEntity<UserVO> getUserInfo(@PathVariable String username) {
        UserDTO user = userService.getUserInfo(username);
        if (user != null) {
            UserVO userVO = ConversionUtils.convert(user, UserVO.class);
            return ResponseEntity.ok(userVO);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<Result> refreshToken(@RequestParam("refresh_token") String refreshToken) {
        try {
            UserDetails user = userService.getUserByUsername(JwtUtils.getClaim(refreshToken, "name"));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            Result result = Result.ok().setData(JwtUtils.refreshToken(refreshToken));
            return ResponseEntity.ok(result);
        }catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.badRequest().body(Result.error("refresh token failed"));
        }
    }

    @PutMapping("/info/update")
    public ResponseEntity<Result> updateUserInfo(@RequestParam("username") String username, @RequestParam("avatar") String avatar, @RequestParam("email") String email, @AuthenticationPrincipal UserDO user) {
        try {
            user.setUsername(username);
            user.setAvatar(avatar);
            user.setEmail(email);
            userService.updateById(user);
            return ResponseEntity.ok(Result.ok("updated user"));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(Result.error("update user info failed"));
        }
    }
}
