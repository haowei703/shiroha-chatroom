package com.shiroha.chatroom.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.shiroha.chatroom.domain.ChatMessageDO;
import com.shiroha.chatroom.dto.ChatMessageDTO;
import com.shiroha.chatroom.mapper.ChatMapper;
import com.shiroha.chatroom.service.ChatService;
import com.shiroha.chatroom.service.FileService;
import com.shiroha.chatroom.types.*;
import com.shiroha.chatroom.types.AudioMsgJsonResponse;
import com.shiroha.chatroom.types.FileMsgJsonResponse;
import com.shiroha.chatroom.types.ImageMsgJsonResponse;
import com.shiroha.chatroom.types.TextMsgJsonResponse;
import com.shiroha.chatroom.utils.ImageAndAudioUtils;
import com.shiroha.chatroom.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ChatServiceImpl extends ServiceImpl<ChatMapper, ChatMessageDO> implements ChatService {

    private final ChatMapper chatMapper;

    private final FileService fileService;

    @Override
    @Transactional
    public boolean saveMessage(ChatMessage<?> message) {
        if(message instanceof AbstractChatMessage<?> chatMessage) {
            if(chatMessage.getTimestamp() == null) {
                throw new IllegalArgumentException("Timestamp is not allowed to be null");
            }
            if(chatMessage.getMessageType() == null) {
                throw new IllegalArgumentException("MessageType is not allowed to be null");
            }
        }

        ChatMessageDO chatMessageDO;
        if(message instanceof TextChatMessage textMessage) {
            chatMessageDO = textMessage.toChatMessageDO();
            chatMessageDO.setContent(textMessage.getData());
        } else if(message instanceof BinaryChatMessage binaryChatMessage) {
            chatMessageDO = binaryChatMessage.toChatMessageDO();
            MessageType type = binaryChatMessage.getMessageType();

            if(type == MessageType.IMAGE) {
                // 图像保存图像宽高信息
                int[] arr = ImageAndAudioUtils.getImageInfo(binaryChatMessage.getData().array());
                Map<String, Object> map = new HashMap<>();
                map.put("width", arr[0]);
                map.put("height", arr[1]);

                chatMessageDO.setContent(JsonUtils.serialize(map));
                chatMessageDO.setData(binaryChatMessage.getData().array());
            } else if(type == MessageType.AUDIO) {
                chatMessageDO.setContent(binaryChatMessage.getFileMetaData().getContentType());
                chatMessageDO.setData(binaryChatMessage.getData().array());
            } else if(type == MessageType.FILE) {
                fileService.saveToFileSystem(binaryChatMessage);
                // 文件名作为content内容
                chatMessageDO.setContent(binaryChatMessage.getFileMetaData().getFileName());
            }
        }else throw new RuntimeException("Message not supported");

        try {
            chatMapper.insert(chatMessageDO);
            return true;
        }catch (Exception e) {
            log.error("Failed to save message", e);
            return false;
        }
    }

    @Override
    public ChatMessageDTO getMessageByPage(ChatMessageDTO chatMessageDTO) {
        Page<ChatMessageDO> page = new Page<>(chatMessageDTO.getPageNum(), chatMessageDTO.getPageSize());
        UUID senderId = chatMessageDTO.getSenderId();
        UUID receiverId = chatMessageDTO.getReceiverId();
        Long groupId = chatMessageDTO.getGroupId();


        boolean isPrivate = receiverId != null && groupId == null;
        IPage<ChatMessageDO> doPage = isPrivate ? chatMapper.searchPrivateChatMessageByPage(page, senderId, receiverId)
                : chatMapper.searchGroupChatMessageByPage(page, senderId, groupId);

        if(doPage == null) {
            throw new RuntimeException("receiverId or groupId must not be null");
        }

        chatMessageDTO.setMsgJsonResponseList(getMsgJsonResponseList(doPage));

        chatMessageDTO.setTotalPages(doPage.getPages());
        chatMessageDTO.setTotalCounts(doPage.getTotal());
        chatMessageDTO.setPageNum(doPage.getCurrent());
        chatMessageDTO.setPageSize(doPage.getSize());

        return chatMessageDTO;
    }

    @Override
    public String getFilename(UUID senderId, UUID receiverId, LocalDateTime timestamp) {
        LambdaQueryWrapper<ChatMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessageDO::getSenderId, senderId)
                .eq(ChatMessageDO::getReceiverId, receiverId)
                .eq(ChatMessageDO::getCreatedAt, timestamp);
        return chatMapper.selectOne(queryWrapper).getContent();
    }

    private List<MsgJsonResponse<?>> getMsgJsonResponseList(IPage<ChatMessageDO> doPage) {
        List<ChatMessageDO> doList = doPage.getRecords();
        return doList.stream()
                .map(chatMessageDO -> {
            if(chatMessageDO.getMessageType() == MessageType.TEXT) {
                // 文本消息
                TextMsgJsonResponse response = (TextMsgJsonResponse) setMessageInfo(TextMsgJsonResponse.builder(), chatMessageDO);
                response.setContent(TextMsgJsonResponseContent.builder()
                        .data(chatMessageDO.getContent())
                        .build());
                return response;
            }else if(chatMessageDO.getMessageType() == MessageType.IMAGE) {
                // 图片消息
                String content = chatMessageDO.getContent();

                Map<String, Integer> map = JsonUtils.deserialize(content, new TypeReference<>() {});

                long width = map.get("width");
                long height = map.get("height");

                ImageMsgJsonResponse response = (ImageMsgJsonResponse) setMessageInfo(ImageMsgJsonResponse.builder(), chatMessageDO);
                response.setContent(ImageMsgJsonResponseContent.builder()
                        .data(ByteBuffer.wrap(chatMessageDO.getData()))
                        .width(width)
                        .height(height)
                        .build());
                return response;
            }else if(chatMessageDO.getMessageType() == MessageType.AUDIO) {

                // 语音消息
                AudioMsgJsonResponse response = (AudioMsgJsonResponse) setMessageInfo(AudioMsgJsonResponse.builder(), chatMessageDO);
                ByteBuffer data = ByteBuffer.wrap(chatMessageDO.getData());
                response.setContent(AudioMsgJsonResponseContent.builder()
                        .encoding(chatMessageDO.getContent())
                        .data(data)
                        .build());
                return response;
            }else if(chatMessageDO.getMessageType() == MessageType.FILE) {
                // 文件消息
                FileMsgJsonResponse response = (FileMsgJsonResponse) setMessageInfo(FileMsgJsonResponse.builder(), chatMessageDO);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String downloadUrl = "http://localhost:8080/api/v1/file/download?sender_id=%s&timestamp=%s".formatted(
                        chatMessageDO.getSenderId(), chatMessageDO.getCreatedAt().format(formatter));
                response.setContent(FileMsgJsonResponseContent.builder()
                        // 文件名
                        .fileName(chatMessageDO.getContent())
                        .url(downloadUrl)
                        // 文件大小
                        .build());
                return response;
            }throw new IllegalArgumentException("MessageType not supported");
        })
                .collect(Collectors.toList());
    }

    private MsgJsonResponse<?> setMessageInfo(AbstractMsgJsonResponse.AbstractMsgJsonResponseBuilder<?, ?, ?> builder, ChatMessageDO chatMessageDO) {
        return builder
                .id(chatMessageDO.getId())
                .senderId(chatMessageDO.getSenderId().toString())
                .receiverId(String.valueOf(chatMessageDO.getReceiverId()))
                .groupId(String.valueOf(chatMessageDO.getGroupId()))
                .messageType(chatMessageDO.getMessageType())
                .createdAt(chatMessageDO.getCreatedAt())
                .status(chatMessageDO.getStatus())
                .build();
    }

    /*
                                    _ooOoo_
                                   o8888888o
                                   88" . "88
                                   (| -_- |)
                                   O\  =  /O
                                ____/`---'\____
                              .'  \\|     |//  `.
                             /  \\|||  :  |||//  \
                            /  _||||| -:- |||||-  \
                            |   | \\\  -  /// |   |
                            | \_|  ''\---/''  |   |
                            \  .-\__  `-`  ___/-. /
                          ___`. .'  /--.--\  `. . __
                       ."" '<  `.___\_<|>_/___.'  >'"".
                      | | :  `- \`.;`\ _ /`;.`/ - ` : | |
                      \  \ `-.   \_ __\ /__ _/   .-` /  /
                 ======`-.____`-.___\_____/___.-`____.-'======
                                    `=---='
                 ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                            佛祖保佑        永无BUG
                   佛曰:
                          写字楼里写字间，写字间里程序员；
                          程序人员写程序，又拿程序换酒钱。
                          酒醒只在网上坐，酒醉还来网下眠；
                          酒醉酒醒日复日，网上网下年复年。
                          但愿老死电脑间，不愿鞠躬老板前；
                          奔驰宝马贵者趣，公交自行程序员。
                          别人笑我忒疯癫，我笑自己命太贱；
                          不见满街漂亮妹，哪个归得程序员？
       */
}
