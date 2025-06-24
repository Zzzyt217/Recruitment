package com.test.Service.Impl;

import com.rabbitmq.client.Channel;
import com.test.Config.RabbitMQConfig;
import com.test.Pojo.EmailMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;

/**
 * 邮件消息消费者服务
 */
@Service
public class EmailConsumer {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * 处理邮件消息
     */
    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void processEmailMessage(EmailMessage message, Channel channel, 
                                  @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            System.out.println("收到邮件发送请求: " + message.getTo());
            
            // 构建MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(message.getTo());
            helper.setSubject(message.getSubject());
            
            // 根据模板类型设置不同的邮件内容
            String emailContent = generateEmailContent(message);
            helper.setText(emailContent, true);
            helper.setFrom("zzzyt217@163.com");

            // 发送邮件
            mailSender.send(mimeMessage);
            
            System.out.println("邮件发送成功: " + message.getTo());
            
            // 确认消息处理完成
            channel.basicAck(tag, false);
        } catch (Exception e) {
            try {
                System.err.println("邮件发送失败: " + e.getMessage());
                // 消息处理失败，重新入队
                channel.basicNack(tag, false, true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * 根据模板生成邮件内容
     */
    private String generateEmailContent(EmailMessage message) {
        String templateId = message.getTemplateId();
        Map<String, Object> params = message.getParams();
        
        // 根据不同的模板ID生成不同的邮件内容
        if ("offer_notification".equals(templateId)) {
            // 录用通知模板
            String candidateName = (String) params.get("candidateName");
            String positionTitle = (String) params.get("positionTitle");
            String companyName = (String) params.get("companyName");
            String offerDate = (String) params.get("offerDate");
            
            return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0;'>" +
                   "<h2 style='color: #4361ee;'>录用通知</h2>" +
                   "<p>尊敬的 " + candidateName + "，</p>" +
                   "<p>恭喜您！我们很高兴地通知您，您已被 <strong>" + companyName + "</strong> 录用为 <strong>" + positionTitle + "</strong> 职位。</p>" +
                   "<p>您的能力、经验和个人素质给我们留下了深刻的印象，我们相信您将成为我们团队的宝贵资产。</p>" +
                   "<p>我们将在近期与您联系，讨论入职事宜和后续安排。</p>" +
                   "<p>如有任何问题，请随时与我们的HR团队联系。</p>" +
                   "<p>期待您的加入！</p>" +
                   "<p style='margin-top: 30px;'>此致，<br>HR团队<br>" + companyName + "<br>" + offerDate + "</p>" +
                   "</div>";
        } else if ("interview_invitation".equals(templateId)) {
            // 面试邀请模板
            String candidateName = (String) params.get("candidateName");
            String positionTitle = (String) params.get("positionTitle");
            String companyName = (String) params.get("companyName");
            String interviewTime = (String) params.get("interviewTime");
            String location = (String) params.get("location");
            
            return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0;'>" +
                   "<h2 style='color: #4361ee;'>面试邀请</h2>" +
                   "<p>尊敬的 " + candidateName + "，</p>" +
                   "<p>感谢您对 <strong>" + positionTitle + "</strong> 职位的申请。我们已经审阅了您的简历，希望邀请您参加面试。</p>" +
                   "<p><strong>面试详情：</strong></p>" +
                   "<ul>" +
                   "<li><strong>公司：</strong>" + companyName + "</li>" +
                   "<li><strong>职位：</strong>" + positionTitle + "</li>" +
                   "<li><strong>时间：</strong>" + interviewTime + "</li>" +
                   "<li><strong>地点：</strong>" + location + "</li>" +
                   "</ul>" +
                   "<p>请您提前15分钟到达，并携带您的身份证件和简历。</p>" +
                   "<p>如果您无法按时参加面试，请提前与我们联系重新安排时间。</p>" +
                   "<p>期待与您的见面！</p>" +
                   "<p style='margin-top: 30px;'>此致，<br>HR团队<br>" + companyName + "</p>" +
                   "</div>";
        } else {
            // 默认模板
            return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0;'>" +
                   "<h2 style='color: #4361ee;'>通知</h2>" +
                   "<p>您好，</p>" +
                   "<p>这是一封来自招聘系统的通知邮件。</p>" +
                   "<p>感谢您的关注！</p>" +
                   "</div>";
        }
    }
} 