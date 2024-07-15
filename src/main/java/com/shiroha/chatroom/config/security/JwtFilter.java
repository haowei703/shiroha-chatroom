package com.shiroha.chatroom.config.security;

import com.nimbusds.jose.JOSEException;
import com.shiroha.chatroom.service.Impl.UserServiceImpl;
import com.shiroha.chatroom.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserServiceImpl userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = getTokenFromRequest(request, response);
        if (jwtToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtToken.isEmpty() && JwtUtils.verifyToken(jwtToken)) {
                String isAccessToken = JwtUtils.getClaim(jwtToken, "isAccessToken");

                if(JwtUtils.isTokenExpired(jwtToken)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired");
                    filterChain.doFilter(request, response);
                    return;
                }

                if (!isAccessToken.equals("true")) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh Token is not allowed");
                    filterChain.doFilter(request, response);
                    return;
                }

                String username = JwtUtils.getClaim(jwtToken, "name");
                UserDetails user = userService.loadUserByUsername(username);
                if (user != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } else {
                log.error("Invalid JWT token.");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            }

        } catch (JOSEException | ParseException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request, HttpServletResponse response) {
        final String authHeader = request.getHeader("Authorization");
        // 先检查 Authorization 请求头是否携带token
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            // 否则检查 WebSocket 子协议头是否包含token
            final String SecWebSocketProtocol = request.getHeader("Sec-WebSocket-Protocol");
            if (SecWebSocketProtocol == null || SecWebSocketProtocol.isEmpty()) {
                return null;
            }

            // response设置子协议头，接受连接
            response.setHeader("Sec-WebSocket-Protocol", SecWebSocketProtocol);
            return SecWebSocketProtocol;
        }
        return authHeader.substring(7);
    }
}
