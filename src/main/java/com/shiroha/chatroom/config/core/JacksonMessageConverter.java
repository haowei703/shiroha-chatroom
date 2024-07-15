package com.shiroha.chatroom.config.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class JacksonMessageConverter implements MessageConverter {

    private final ObjectMapper objectMapper;

    @Override
    public Message toMessage(@NonNull Object o, @NonNull MessageProperties messageProperties) throws MessageConversionException {
        try {
            String json = objectMapper.writeValueAsString(o);
            messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            messageProperties.setContentEncoding("UTF-8");
            messageProperties.setHeader("type", o.getClass().getName()); // 添加类型信息
            return new Message(json.getBytes(), messageProperties);
        } catch (JsonProcessingException e) {
            throw new MessageConversionException("Failed to convert object to Message", e);
        }
    }

    @Override
    public Object fromMessage(@NonNull Message message) throws MessageConversionException {
        try {
            String json = new String(message.getBody());
            String typeName = message.getMessageProperties().getHeader("type");
            if (typeName == null) {
                throw new MessageConversionException("Message type information is missing");
            }
            Class<?> clazz = Class.forName(typeName);
            JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException | ClassNotFoundException e) {
            throw new MessageConversionException("Failed to convert Message to object", e);
        }
    }
}
