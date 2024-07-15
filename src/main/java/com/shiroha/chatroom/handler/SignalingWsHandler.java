package com.shiroha.chatroom.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebRTC信令交换处理器
 */
@Component
@Slf4j
public class SignalingWsHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        // 添加新的WebSocket会话
        sessions.put(session.getId(), session);
        log.info("New connection established, session ID: {}", session.getId());
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        // 处理接收到的信令消息
        String payload = message.getPayload();
        log.info("Received message from session {}: {}", session.getId(), payload);

        // 将信令消息转发给其他客户端
        sessions.values().forEach(webSocketSession -> {
            if (!webSocketSession.getId().equals(session.getId())) {
                try {
                    webSocketSession.sendMessage(new TextMessage(payload));
                } catch (Exception e) {
                    log.error("Error sending message to session {}: {}", webSocketSession.getId(), e.getMessage());
                }
            }
        });
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        // 移除断开的WebSocket会话
        sessions.remove(session.getId());
        log.info("Connection closed, session ID: {}", session.getId());
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
        // 处理传输错误
        log.error("Transport error in session {}: {}", session.getId(), exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }
}
