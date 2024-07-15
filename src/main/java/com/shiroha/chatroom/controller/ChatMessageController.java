package com.shiroha.chatroom.controller;

import com.shiroha.chatroom.domain.UserDO;
import com.shiroha.chatroom.dto.ChatMessageDTO;
import com.shiroha.chatroom.handler.MessageEventPublisher;
import com.shiroha.chatroom.producer.ChatQueueProducer;
import com.shiroha.chatroom.service.ChatService;
import com.shiroha.chatroom.service.FriendService;
import com.shiroha.chatroom.service.GroupMemberService;
import com.shiroha.chatroom.types.BinaryChatMessage;
import com.shiroha.chatroom.types.FileMetaData;
import com.shiroha.chatroom.types.MessageType;
import com.shiroha.chatroom.utils.ConversionUtils;
import com.shiroha.chatroom.utils.Result;
import com.shiroha.chatroom.vo.ChatMessageVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/v1/messages")
public class ChatMessageController {

    private final ChatService chatService;

    private final ChatQueueProducer producer;

    private final FriendService friendService;

    private final GroupMemberService groupMemberService;

    private final MessageEventPublisher publisher;

    // 获取私聊消息和群聊消息
    @GetMapping
    public ResponseEntity<ChatMessageVO> getMessages(@RequestParam("pageNum") long pageNum,
                                     @RequestParam("pageSize") long pageSize,
                                     @RequestParam("type") String type,
                                     @RequestParam(value = "user_id", required = false) String userId,
                                     @RequestParam(value = "group_id", required = false) String groupId,
                                     @AuthenticationPrincipal UserDO user) {
        try {
            ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .senderId(user.getId())
                    .build();

            validateRequest(user.getId(), type, userId, groupId,
                    chatMessageDTO::setReceiverId,
                    chatMessageDTO::setGroupId);

            ChatMessageDTO response = chatService.getMessageByPage(chatMessageDTO);
            if(response.getMsgJsonResponseList().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ConversionUtils.convert(response, ChatMessageVO.class));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // 发送文件、图片、音频等特殊消息
    @PostMapping("/send")
    public ResponseEntity<Result> sendBinaryMessage(@RequestParam("file") MultipartFile file,
                                                    @RequestParam("type") String type,
                                                    @RequestParam(value = "user_id", required = false) String userId,
                                                    @RequestParam(value = "group_id", required = false) String groupId,
                                                    @AuthenticationPrincipal UserDO user) {

        try {
            BinaryChatMessage message = BinaryChatMessage.builder()
                    // 文件元数据
                    .fileMetaData(FileMetaData.fromMultipartFile(file))
                    // 消息类型
                    .messageType(getMessageType(file))
                    // 数据
                    .data(ByteBuffer.wrap(file.getBytes()))
                    // 必须手动设置时间，关联了文件的命名逻辑
                    .timestamp(LocalDateTime.now().withNano(0))
                    .senderId(user.getId())
                    .build();
            // 发送方只能为请求用户
            validateRequest(user.getId(), type, userId, groupId,
                    message::setReceiverId,
                    message::setGroupId);

            producer.sendMessage(message);

            // 发布事件，通过websocket信道通知用户新消息
            publisher.publish(message, type);

            return ResponseEntity.ok(Result.ok("successfully sent"));
        } catch (IOException e){
            log.error("IO Error", e);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e){
            return ResponseEntity.status(401).build();
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    private MessageType getMessageType(MultipartFile file) {
        String contentType = file.getContentType();
        if(contentType == null) {
            throw new RuntimeException("content type is null");
        }
        if(contentType.startsWith("image")) {
            return MessageType.IMAGE;
        }else if(contentType.startsWith("audio")) {
            return MessageType.AUDIO;
        }else {
            return MessageType.FILE;
        }
    }

    /**
     * 使用匿名函数检验请求合法性
     * @param rqUser 上下文保存的用户id，即发起请求的用户id
     * @param type 请求类型，如私聊还是群聊
     * @param groupId 群组id，消息为群消息时作为回调函数的返回值
     * @param tgUser 聊天记录的目标用户，消息的发送发或接收方，消息为私聊时作为回调函数的返回值
     * @param isPrivate 消息为私聊时的回调函数
     * @param isGroup 消息为群聊时的回调函数
     * @throws RuntimeException 请求不合法时抛出
     */
    private void validateRequest(UUID rqUser, String type, String tgUser, String groupId,
                                 Consumer<UUID> isPrivate, Consumer<Long> isGroup) throws RuntimeException {

        if(Objects.equals(type, "P") && friendService.isFriend(rqUser, UUID.fromString(tgUser))) {
            isPrivate.accept(UUID.fromString(tgUser));
        }else if(Objects.equals(type, "G") && groupMemberService.isUserMemberOfGroup(rqUser, Long.valueOf(groupId))) {
            isGroup.accept(Long.valueOf(groupId));
        }else {
            throw new RuntimeException("Invalid request");
        }
    }
}
