package com.shiroha.chatroom.service;

import com.shiroha.chatroom.types.BinaryChatMessage;
import org.springframework.core.io.Resource;

import java.net.MalformedURLException;
import java.time.LocalDateTime;

public interface FileService {

    /**
     * 将文件保存至本地
     * @param chatMessage 二进制消息
     */
    void saveToFileSystem(BinaryChatMessage chatMessage);

    /**
     * 根据文件路径读取文件
     * @param senderId 发送者
     * @param timestamp 发送时间
     * @return 读取的文件资源路径
     */
    String readFromFileSystem(String senderId, LocalDateTime timestamp) throws MalformedURLException;
}
