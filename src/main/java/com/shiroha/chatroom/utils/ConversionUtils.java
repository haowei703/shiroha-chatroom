package com.shiroha.chatroom.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象转换工具类
 */
@Slf4j
public class ConversionUtils {

    /**
     * 源对象转换为目标类型的对象
     * @param source 源对象
     * @param targetClass 目标类型
     * @param <T> 目标对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        T targetInstance = null;
        try {
            targetInstance = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, targetInstance);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return targetInstance;
    }

    /**
     * 源对象列表转换为目标类型的对象列表
     * @param source 源对象列表
     * @param targetClass 目标类型
     * @return 转换后的目标类型的对象列表
     * @param <T> 源对象
     * @param <U> 目标对象
     */
    public static <T, U> List<U> convertList(List<T> source, Class<U> targetClass) {
        return source.stream()
                .map(element -> convert(element, targetClass))
                .collect(Collectors.toList());
    }
}
