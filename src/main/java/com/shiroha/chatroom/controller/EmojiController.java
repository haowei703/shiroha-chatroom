package com.shiroha.chatroom.controller;

import com.shiroha.chatroom.domain.EmojiDO;
import com.shiroha.chatroom.service.EmojiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/emoji")
@AllArgsConstructor
public class EmojiController {

    private final EmojiService emojiService;

    @GetMapping
    public List<EmojiDO> getAllEmoji() {
        return emojiService.list();
    }
}
