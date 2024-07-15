package com.shiroha.chatroom.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;


/**
 * json序列化反序列化工具类
 */
@Slf4j
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将任意对象序列化为 JSON 字符串
     * @param object 要序列化的对象
     * @return 序列化后的 JSON 字符串
     */
    public static String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        }catch (JsonProcessingException e) {
            log.error("Json serialization error", e);
            throw new RuntimeException("Json serialization error", e);
        }
    }

    /**
     * 将任意对象序列化为泛型类型
     * @param object 要序列化的对象
     * @return 序列化后的对象
     */
    public static <T> T serialize(Object object, TypeReference<T> typeReference) {
        try {
            return objectMapper.convertValue(object, typeReference);
        }catch (IllegalArgumentException e) {
            log.error("Json serialization error", e);
            throw new RuntimeException("Json serialization error", e);
        }
    }

    /**
     * 将 JSON 字符串解析为对象，适用于普通类型
     * @param json JSON 字符串
     * @return 解析后的对象
     */
    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(clazz, Object.class);
            return objectMapper.readValue(json, javaType);
        }catch (JsonProcessingException e) {
            log.error("Json deserialization error", e);
            throw new RuntimeException("Json deserialization error", e);
        }
    }


    /**
     * 反序列化含有泛型参数的对象
     * @param json JSON 字符串
     * @param typeReference Jackson内置类型，用于保留泛型信息
     * @return 解析后的对象
     * @param <T> 泛型参数
     */
    public static <T> T deserialize(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        }catch (JsonProcessingException e) {
            log.error("Json deserialization error", e);
            throw new RuntimeException("Json deserialization error", e);
        }
    }
}
