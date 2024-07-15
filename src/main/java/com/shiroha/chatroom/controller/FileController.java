package com.shiroha.chatroom.controller;

import com.shiroha.chatroom.domain.UserDO;
import com.shiroha.chatroom.service.ChatService;
import com.shiroha.chatroom.service.FileService;
import com.shiroha.chatroom.service.FriendService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/file")
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    private final FriendService friendService;

    private final ChatService chatService;

    @GetMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Resource> download(@RequestParam("sender_id") String senderId, @RequestParam("timestamp") String timestamp, @AuthenticationPrincipal UserDO user) {
        if(!friendService.isFriend(user.getId(), UUID.fromString(senderId))) {
            return ResponseEntity.status(401).build();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(timestamp, formatter);
        try {
            String localFilePath= fileService.readFromFileSystem(senderId, localDateTime);
            String filename = chatService.getFilename(UUID.fromString(senderId), user.getId(), localDateTime);

            Path path = Paths.get(localFilePath);
            FileSystemResource file = new FileSystemResource(path);
            String contentDisposition = ContentDisposition
                    .builder("attachment")
                    .filename(filename)
                    .build().toString();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.contentLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(file);
        }catch (IOException e) {
            log.error("IO Exception", e);
            return ResponseEntity.notFound().build();
        }
    }
}
