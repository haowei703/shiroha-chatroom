package com.shiroha.chatroom.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiroha.chatroom.domain.EmojiDO;
import com.shiroha.chatroom.mapper.EmojiMapper;
import com.shiroha.chatroom.service.EmojiService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmojiServiceImpl extends ServiceImpl<EmojiMapper, EmojiDO> implements EmojiService {
}
