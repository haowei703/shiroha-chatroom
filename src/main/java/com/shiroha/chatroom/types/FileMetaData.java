package com.shiroha.chatroom.types;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件元数据
 */
@Data
public class FileMetaData {
    private String fileName;
    private String extension;
    private long size;
    private String contentType;

    private FileMetaData(String fileName, String extension, long size, String contentType) {
        this.fileName = fileName;
        this.extension = extension;
        this.size = size;
        this.contentType = contentType;
    }

    // 静态工厂方法
    public static FileMetaData fromMultipartFile(MultipartFile file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("MultipartFile cannot be null");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }

        return new FileMetaData(
                originalFilename,
                extension,
                file.getSize(),
                file.getContentType()
        );
    }
}
