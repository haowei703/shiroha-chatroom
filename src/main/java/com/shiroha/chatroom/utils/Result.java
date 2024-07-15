package com.shiroha.chatroom.utils;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * restful风格响应封装类
 */
@Data
@Builder
public class Result implements Serializable {

    private Integer code;
    private String msg;
    private Object data;

    public static Result ok(String msg) {
        return Result.builder().code(200).msg(msg).build();
    }

    public static Result ok() {
        return Result.builder().code(200).msg("success").build();
    }

    public static Result error(String msg) {
        return Result.builder().code(500).msg(msg).build();
    }

    public static Result error() {
        return Result.builder().code(500).msg("fail").build();
    }

    public <T> Result setData(T data) {
        this.data = data;
        return this;
    }
}