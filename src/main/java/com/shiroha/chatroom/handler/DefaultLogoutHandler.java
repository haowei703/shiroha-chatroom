package com.shiroha.chatroom.handler;

import com.shiroha.chatroom.domain.UserDO;
import com.shiroha.chatroom.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DefaultLogoutHandler implements LogoutHandler {

    private final UserService userService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication auth = securityContext.getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDO user) {
            userService.logout(user.getId());
        }
        securityContext.setAuthentication(null);
    }
}
