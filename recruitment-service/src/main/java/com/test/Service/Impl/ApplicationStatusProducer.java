package com.test.Service.Impl;

import com.test.Config.RabbitMQConfig;
import com.test.Pojo.ApplicationStatusChangeMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 申请状态变更消息生产者服务
 */
@Service
public class ApplicationStatusProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送申请状态变更消息
     */
    public void sendStatusChangeMessage(Long applicationId, String newStatus) {
        // 构建申请状态变更消息对象
        ApplicationStatusChangeMessage message = new ApplicationStatusChangeMessage();
        message.setApplicationId(applicationId);
        message.setNewStatus(newStatus);
        message.setUpdateTime(new Date());
        
        // 发送消息到RabbitMQ
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTIFICATION_EXCHANGE,
            RabbitMQConfig.APPLICATION_STATUS_ROUTING_KEY,
            message
        );
        
        System.out.println("申请状态变更消息已发送: applicationId=" + applicationId + ", newStatus=" + newStatus);
    }
} 