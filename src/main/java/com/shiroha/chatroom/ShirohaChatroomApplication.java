package com.shiroha.chatroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ShirohaChatroomApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShirohaChatroomApplication.class, args);
    }

}
