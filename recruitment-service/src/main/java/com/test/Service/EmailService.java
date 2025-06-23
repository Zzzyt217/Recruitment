package com.test.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @PostConstruct
    public void init() {
        System.out.println("\n===== EmailService初始化 =====");
        System.out.println("JavaMailSender注入状态: " + (mailSender != null ? "成功" : "失败"));
        System.out.println("发件人邮箱: " + fromEmail);
        System.out.println("===== EmailService初始化完成 =====\n");
    }
    
    /**
     * 发送简单文本邮件
     */
    public boolean sendSimpleMail(String to, String subject, String content) {
        try {
            System.out.println("\n===== 开始发送邮件 =====");
            System.out.println("检查JavaMailSender: " + (mailSender != null ? "已注入" : "未注入 - 这是问题!"));
            System.out.println("发件人: " + fromEmail);
            System.out.println("收件人: " + to);
            System.out.println("主题: " + subject);
            System.out.println("内容: " + content);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            System.out.println("邮件对象创建完成，准备发送...");
            
            mailSender.send(message);
            
            System.out.println("邮件已发送，无异常");
            System.out.println("===== 邮件发送完成 =====\n");
            return true;
        } catch (Exception e) {
            System.err.println("\n===== 邮件发送失败 =====");
            System.err.println("错误类型: " + e.getClass().getName());
            System.err.println("错误信息: " + e.getMessage());
            System.err.println("堆栈跟踪:");
            e.printStackTrace();
            System.err.println("===== 邮件错误信息结束 =====\n");
            return false;
        }
    }
    
    /**
     * 发送录用通知邮件
     */
    public boolean sendOfferNotification(String to, String candidateName, String positionTitle, String companyName) {
        System.out.println("\n===== 准备发送录用通知 =====");
        System.out.println("收件人: " + to);
        System.out.println("候选人: " + candidateName);
        System.out.println("职位: " + positionTitle);
        System.out.println("公司: " + companyName);
        
        String subject = "恭喜您！" + companyName + "录用通知";
        
        String content = "尊敬的 " + candidateName + "：\n\n" +
                "恭喜您！您申请的 " + companyName + " 公司的 " + positionTitle + " 职位已获得录用。\n\n" +
                "请尽快登录系统查看录用详情，并按照提示完成后续流程。\n\n" +
                "期待您的加入！\n\n" +
                companyName + " 招聘团队";
        
        boolean result = sendSimpleMail(to, subject, content);
        System.out.println("录用通知邮件发送结果: " + (result ? "成功" : "失败"));
        System.out.println("===== 录用通知处理完成 =====\n");
        
        return result;
    }
} 