package com.shiroha.chatroom.config.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableRabbit
@AllArgsConstructor
public class RabbitMQConfig {

    private final ObjectMapper objectMapper;

    @Bean("chatQueue")
    public Queue chatQueue() {
        return new Queue("chat-queue");
    }

    @Bean("memberQueue")
    public Queue memberQueue() {
        return new Queue("member-queue");
    }

    @Bean("chatExchange")
    public DirectExchange chatExchange() {
        return new DirectExchange("chat-exchange");
    }

    @Bean("memberExchange")
    public DirectExchange memberExchange() {
        return new DirectExchange("member-exchange");
    }

    @Bean
    public Binding chatBinding(@Qualifier("chatQueue") Queue chatQueue, @Qualifier("chatExchange") DirectExchange chatExchange) {
        return BindingBuilder.bind(chatQueue).to(chatExchange).with("chat-queue");
    }

    @Bean
    public Binding memberBinding(@Qualifier("memberQueue") Queue memberQueue, @Qualifier("memberExchange") DirectExchange memberExchange) {
        return BindingBuilder.bind(memberQueue).to(memberExchange).with("member-queue");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}
