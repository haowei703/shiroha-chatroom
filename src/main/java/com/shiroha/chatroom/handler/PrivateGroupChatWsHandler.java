package com.shiroha.chatroom.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shiroha.chatroom.helper.WebSocketSessionManager;
import com.shiroha.chatroom.producer.ChatQueueProducer;
import com.shiroha.chatroom.types.*;
import com.shiroha.chatroom.utils.ImageAndAudioUtils;
import com.shiroha.chatroom.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 聊天信道
 */
@Slf4j
public class PrivateGroupChatWsHandler extends TextWebSocketHandler {

    private WebSocketSessionManager manager;

    private ChatQueueProducer producer;

    // 事件监听器
    @EventListener
    public void handleMessageEvent(MessageEvent event) {
        String type = event.getType();
        BinaryChatMessage message = event.getChatMessage();
        log.info("Received message {}", message);

        // 接口重调
        AbstractMsgJsonResponse<?> response = null;
        switch (message.getMessageType()) {
            case IMAGE -> {
                int[] imageInfo = ImageAndAudioUtils.getImageInfo(message.getData().array());
                ImageMsgJsonResponseContent content = ImageMsgJsonResponseContent.builder()
                        .width(imageInfo[0])
                        .height(imageInfo[1])
                        .data(message.getData())
                        .build();
                response = ImageMsgJsonResponse.builder()
                        .senderId(message.getSenderId().toString())
                        .receiverId(message.getReceiverId() != null ? message.getReceiverId().toString() : null)
                        .groupId(message.getGroupId() != null ? String.valueOf(message.getGroupId()) : null)
                        .messageType(MessageType.IMAGE)
                        .createdAt(message.getTimestamp())
                        .content(content)
                        .build();
            }
            case AUDIO -> {
                AudioMsgJsonResponseContent content = AudioMsgJsonResponseContent.builder()
                        .data(message.getData())
                        .encoding(message.getFileMetaData().getContentType())
                        .build();
                response = AudioMsgJsonResponse.builder()
                        .senderId(message.getSenderId().toString())
                        .receiverId(message.getReceiverId() != null ? message.getReceiverId().toString() : null)
                        .groupId(message.getGroupId() != null ? String.valueOf(message.getGroupId()) : null)
                        .messageType(MessageType.AUDIO)
                        .createdAt(message.getTimestamp())
                        .content(content)
                        .build();
            }
            case FILE -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String downloadUrl = "http://localhost:8080/api/v1/file/download?sender_id=%s&timestamp=%s".formatted(
                        message.getSenderId(), message.getTimestamp().format(formatter));
                FileMsgJsonResponseContent content = FileMsgJsonResponseContent.builder()
                        .url(downloadUrl)
                        .fileName(message.getFileMetaData().getFileName())
                        .build();
                response = FileMsgJsonResponse.builder()
                        .senderId(message.getSenderId().toString())
                        .receiverId(message.getReceiverId() != null ? message.getReceiverId().toString() : null)
                        .groupId(message.getGroupId() != null ? String.valueOf(message.getGroupId()) : null)
                        .messageType(MessageType.FILE)
                        .createdAt(message.getTimestamp())
                        .content(content)
                        .build();
            }
        }

        // 消息发送目标
        boolean isPrivate = type.equals("P");
        boolean isGroup = type.equals("G");

        // 消息是否发送成功
        boolean success = false;

        try {
            if(isPrivate) {
                success = manager.unicast(message.getSenderId().toString(), message.getReceiverId().toString(), response);
            }else if(isGroup){
                if (response != null) {
                    response.setGroupId(message.getGroupId().toString());
                }
                success = manager.broadcast(message.getSenderId().toString(), message.getGroupId().toString(), response);
            }

            if(!success) {
                log.info("Failed to send message {}", message);
            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Autowired
    public void setManager(WebSocketSessionManager manager) {
        this.manager = manager;
    }

    @Autowired
    public void setChatService(ChatQueueProducer chatQueueProducer) {
        this.producer = chatQueueProducer;
    }

    /**
     * 连接建立之后
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String uerId = session.getAttributes().get("userId").toString();
        log.info("{} is connected", uerId);
        if(!manager.addSession(uerId, session)){
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        String msg = message.getPayload();
        Map<String, String> payload = JsonUtils.deserialize(msg, new TypeReference<>() {});
        if(payload.containsKey("type") && payload.get("type").equals("ping")) {
            Map<String,String> pong = new HashMap<>();
            pong.put("type", "pong");
            session.sendMessage(new TextMessage(JsonUtils.serialize(pong)));
            return;
        }
        if(!payload.containsKey("type") || !payload.containsKey("content")){
            session.sendMessage(new TextMessage("bad message"));
            return;
        }

        // 发送者Id
        String senderId = session.getAttributes().get("userId").toString();
        // 消息内容
        String content = payload.get("content");
        LocalDateTime now = LocalDateTime.now().withNano(0);
        TextMsgJsonResponseContent responseContent = TextMsgJsonResponseContent.builder()
                .data(content)
                .build();
        TextMsgJsonResponse response = TextMsgJsonResponse.builder()
                .senderId(senderId)
                .messageType(MessageType.TEXT)
                .content(responseContent)
                .createdAt(now)
                .build();

        // 消息发送目标
        boolean isPrivate = payload.get("type").equals("P");
        boolean isGroup = payload.get("type").equals("G");

        // 消息是否发送成功
        boolean success = false;

        if(isPrivate) {
            if(!payload.containsKey("receiverId")) return;
            success = manager.unicast(senderId, payload.get("receiverId"), response);
        }else if(isGroup){
            if(!payload.containsKey("groupId")) return;
            response.setGroupId(payload.get("groupId"));
            success = manager.broadcast(senderId, payload.get("groupId"), response);
        }

        TextChatMessage textChatMessage = TextChatMessage.builder()
                .senderId(UUID.fromString(senderId))
                .data(content)
                .timestamp(now)
                .build();
        if(isPrivate) {
            textChatMessage.setReceiverId(UUID.fromString(payload.get("receiverId")));
        }
        if(isGroup) {
            textChatMessage.setGroupId(Long.valueOf(payload.get("groupId")));
        }
        // 生产者发送消息
        producer.sendMessage(textChatMessage);

        if(success) {
            log.info("Successfully sent message {}", message);
        }else {
            session.sendMessage(new TextMessage("failed to send message"));
            log.error("failed to send message");
        }
    }

    @Override
    protected void handlePongMessage(@NonNull WebSocketSession session, @NonNull PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        String uerId = session.getAttributes().get("userId").toString();
        manager.removeSession(uerId);
        log.info("{} is disconnected", uerId);
    }
}
