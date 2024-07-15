package com.shiroha.chatroom.helper;

import com.shiroha.chatroom.types.TokenPair;
import com.shiroha.chatroom.utils.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
@AllArgsConstructor
public class RedisTokenManager {

    private final RedisUtils redisUtils;

    private final static String KEY_PREFIX = "token::";

    public void saveToken(String userId, TokenPair token) {
        Instant now = Instant.now();
        Duration duration = Duration.between(now, token.getExpiresIn());
        redisUtils.setCacheObject(KEY_PREFIX + userId, token.getAccessToken(), (int) duration.getSeconds(), TimeUnit.SECONDS);
    }

    public String getToken(String userId) {
        return redisUtils.getCacheObject(KEY_PREFIX + userId);
    }
}
