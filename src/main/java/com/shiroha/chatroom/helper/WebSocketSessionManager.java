package com.shiroha.chatroom.helper;

import com.shiroha.chatroom.service.GroupMemberService;
import com.shiroha.chatroom.types.MsgJsonResponse;
import com.shiroha.chatroom.utils.JsonUtils;
import com.shiroha.chatroom.utils.RedisUtils;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket会话管理类
 */
@Component
@Slf4j
@AllArgsConstructor
public class WebSocketSessionManager {

    private final RedisUtils redisUtils;

    private final GroupMemberService service;

    // session_id，WebSocketSession
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private static final String HASH_KEY = "userId:sessionId";

    /**
     * 添加会话
     * @param userId 用户id
     * @param session 会话
     * @return true=添加成功；false=添加失败
     */
    public boolean addSession(String userId, WebSocketSession session) {
        if(redisUtils.hasHashKey(HASH_KEY, userId)){
            return false;
        }

        sessions.put(session.getId(), session);
        redisUtils.setCacheMapValue(HASH_KEY, userId, session.getId());
        return true;
    }

    /**
     * 通过userId获取session
     * @param userId 用户id
     * @return WebSocketSession会话
     */
    private WebSocketSession getSessionById(String userId) {
        String sessionId = redisUtils.getCacheMapValue(HASH_KEY, userId);
        return sessions.get(sessionId);
    }

    /**
     * 单播消息
     * @param senderId 发送者id
     * @param receiverId 接收者id
     * @param response 消息内容
     * @return true:消息发送成功;false:消息发送失败
     */
    public boolean unicast(String senderId, String receiverId, MsgJsonResponse<?> response) throws IOException {
        // 获取消息接收者会话
        WebSocketSession recipientSession = getSessionById(receiverId);
        if(recipientSession == null){
            log.info("user {} is offline", receiverId);
            return false;
        }
        if (recipientSession.isOpen()) {
            // 返回序列化后的json
            recipientSession.sendMessage(new TextMessage(JsonUtils.serialize(response)));
            return true;
        }

        log.error("failed to unicast message from {} to {}", senderId, receiverId);
        return false;
    }

    /**
     * 广播消息
     * @param senderId 发送者id
     * @param groupId 接收群聊id
     * @param response 消息
     * @return true:消息发送成功;false:消息发送失败
     */
    public boolean broadcast(String senderId, String groupId, MsgJsonResponse<?> response) {
        List<WebSocketSession> sessions = getSessionInGroup(groupId, senderId);
        try {
            sessions.stream()
                    .filter(WebSocketSession::isOpen)
                    .forEach(session -> safelySendMessage(session, response));
        }catch (Exception e){
            log.error("failed to broadcast message from {} to {}", senderId, groupId);
            return false;
        }

        return true;
    }

    /**
     * 筛选保存的会话中属于组内的成员会话
     * @param groupId 组id
     * @return 会话列表
     */
    private List<WebSocketSession> getSessionInGroup(String groupId, String senderId) {
        Set<String> keys = redisUtils.getHashKeys(HASH_KEY);
        return keys.stream()
                .filter(key -> service.isUserMemberOfGroup(UUID.fromString(key), Long.valueOf(groupId)) && !key.equals(senderId))
                .map(this::getSessionById)
                .filter(Objects::nonNull)
                .toList();
    }

    private void safelySendMessage(WebSocketSession session, MsgJsonResponse<?> response) throws RuntimeException {
        try {
            session.sendMessage(new TextMessage(JsonUtils.serialize(response)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 移除会话
    public void removeSession(String userId) {
        String sessionId = redisUtils.getCacheMapValue(HASH_KEY, userId);
        redisUtils.delCacheMapValue(HASH_KEY, userId);
        sessions.remove(sessionId);
    }

    @PreDestroy
    public void cleanup() {
        redisUtils.deleteObject(HASH_KEY);
    }
}
