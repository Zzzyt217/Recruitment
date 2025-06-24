package com.test.Service.Impl;

import com.test.Config.RabbitMQConfig;
import com.test.Pojo.EmailMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮件消息生产者服务
 */
@Service
public class EmailMessageProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送录用通知邮件消息
     */
    public void sendOfferNotificationMessage(String to, String candidateName, String positionTitle, String companyName) {
        // 构建邮件消息对象
        EmailMessage message = new EmailMessage();
        message.setTo(to);
        message.setSubject("恭喜您！您已获得" + companyName + "的录用通知");
        message.setTemplateId("offer_notification");
        
        Map<String, Object> params = new HashMap<>();
        params.put("candidateName", candidateName);
        params.put("positionTitle", positionTitle);
        params.put("companyName", companyName);
        params.put("offerDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        message.setParams(params);
        
        // 发送消息到RabbitMQ
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTIFICATION_EXCHANGE,
            RabbitMQConfig.EMAIL_ROUTING_KEY,
            message
        );
        
        System.out.println("录用通知邮件已加入发送队列: " + to);
    }
    
    /**
     * 发送面试邀请邮件消息
     */
    public void sendInterviewInvitationMessage(String to, String candidateName, String positionTitle, 
                                               String companyName, Date interviewTime, String location) {
        // 构建邮件消息对象
        EmailMessage message = new EmailMessage();
        message.setTo(to);
        message.setSubject(companyName + "的面试邀请");
        message.setTemplateId("interview_invitation");
        
        Map<String, Object> params = new HashMap<>();
        params.put("candidateName", candidateName);
        params.put("positionTitle", positionTitle);
        params.put("companyName", companyName);
        params.put("interviewTime", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(interviewTime));
        params.put("location", location);
        message.setParams(params);
        
        // 发送消息到RabbitMQ
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.NOTIFICATION_EXCHANGE,
            RabbitMQConfig.EMAIL_ROUTING_KEY,
            message
        );
        
        System.out.println("面试邀请邮件已加入发送队列: " + to);
    }
} 