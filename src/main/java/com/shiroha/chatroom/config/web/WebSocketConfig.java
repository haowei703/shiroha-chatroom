package com.shiroha.chatroom.config.web;

import com.shiroha.chatroom.handler.PrivateGroupChatWsHandler;
import com.shiroha.chatroom.handler.SignalingWsHandler;
import com.shiroha.chatroom.interceptor.ChatHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(privateChatWsHandler(), "/ws/chat")
                .addInterceptors(chatHandshakeInterceptor()).setAllowedOrigins("*");
        registry.addHandler(signalingWsHandler(), "/ws/signaling")
                .addInterceptors(chatHandshakeInterceptor()).setAllowedOrigins("*");
    }

    @Bean
    public PrivateGroupChatWsHandler privateChatWsHandler() {
        return new PrivateGroupChatWsHandler();
    }

    @Bean
    public SignalingWsHandler signalingWsHandler() {
        return new SignalingWsHandler();
    }

    @Bean
    public ChatHandshakeInterceptor chatHandshakeInterceptor() {
        return new ChatHandshakeInterceptor();
    }
}
