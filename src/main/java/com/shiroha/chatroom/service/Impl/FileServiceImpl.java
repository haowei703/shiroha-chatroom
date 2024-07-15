package com.shiroha.chatroom.service.Impl;

import com.shiroha.chatroom.service.FileService;
import com.shiroha.chatroom.types.BinaryChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private static final String UPLOAD_DIR = "resources/upload/";

    @Override
    public void saveToFileSystem(BinaryChatMessage chatMessage) {
        String savePath = generateFilePath(chatMessage);

        if(savePath == null || !writeByBufferToFile(chatMessage.getData(), savePath)){
            throw new RuntimeException("Failed to save file");
        }
    }

    @Override
    public String readFromFileSystem(String senderId, LocalDateTime timestamp) throws MalformedURLException {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = timestamp.format(formatter1);
        Path userDir = Paths.get(UPLOAD_DIR, senderId, date);
        // 计算SHA-256哈希值获取文件名
        String fileName = generateUniqueFileName(timestamp) + ".dat";
        return userDir.resolve(fileName).toString();
    }

    private String generateFilePath(BinaryChatMessage chatMessage) {
        String senderId = chatMessage.getSenderId().toString();
        // 日期用于本地创建独立的目录
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        // 用于生成唯一文件名的时间戳则使用原消息中的timestamp属性来确保没有误差
        LocalDateTime timestamp = chatMessage.getTimestamp();
        Path userDir = Paths.get(UPLOAD_DIR, senderId, date);
        try {
            Files.createDirectories(userDir);
            // 使用时间戳的哈希创建唯一的文件名
            String fileName = generateUniqueFileName(timestamp) + chatMessage.getFileMetaData().getExtension();
            Path filePath = userDir.resolve(fileName);
            return filePath.toString();
        }catch (IOException e){
            log.error("Failed to create directory", e);
            return null;
        }
    }

    private String generateUniqueFileName(LocalDateTime timestamp) {
        try {
            // 计算SHA-256哈希值
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            byte[] hashBytes = digest.digest(timestamp.format(formatter).getBytes(StandardCharsets.UTF_8));

            // 将哈希值转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private boolean writeByBufferToFile(ByteBuffer buffer, String filePath) {
        // 统一文件拓展名为.dat
        String path = filePath.replaceFirst("[.][^.]+$", "") + ".dat";
        try {
            Files.write(Paths.get(path), buffer.array());
            return true;
        } catch (IOException e) {
            log.error("Failed to write ByteBuffer to file {}", filePath, e);
            return false;
        }
    }
}
