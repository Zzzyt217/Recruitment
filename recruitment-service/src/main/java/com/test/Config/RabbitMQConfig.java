package com.test.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    // 邮件通知队列
    public static final String EMAIL_QUEUE = "email.queue";
    // 申请状态变更队列
    public static final String APPLICATION_STATUS_QUEUE = "application.status.queue";
    // 系统通知交换机
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    // 邮件通知路由键
    public static final String EMAIL_ROUTING_KEY = "notification.email";
    // 申请状态变更路由键
    public static final String APPLICATION_STATUS_ROUTING_KEY = "notification.application.status";
    
    // 邮件队列
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }
    
    // 申请状态队列
    @Bean
    public Queue applicationStatusQueue() {
        return new Queue(APPLICATION_STATUS_QUEUE, true);
    }
    
    // 通知交换机
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }
    
    // 邮件队列绑定
    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(notificationExchange()).with(EMAIL_ROUTING_KEY);
    }
    
    // 申请状态队列绑定
    @Bean
    public Binding applicationStatusBinding() {
        return BindingBuilder.bind(applicationStatusQueue()).to(notificationExchange()).with(APPLICATION_STATUS_ROUTING_KEY);
    }
    
    // 消息转换器
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    // 配置RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
} 