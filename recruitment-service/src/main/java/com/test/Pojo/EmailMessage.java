package com.test.Pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮件消息模型类
 */
public class EmailMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // 邮件接收者
    private String to;
    // 邮件主题
    private String subject;
    // 邮件模板ID
    private String templateId;
    // 模板参数
    private Map<String, Object> params = new HashMap<>();

    public EmailMessage() {
    }

    public EmailMessage(String to, String subject, String templateId) {
        this.to = to;
        this.subject = subject;
        this.templateId = templateId;
    }
    
    // Getters and Setters
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    
    public Map<String, Object> getParams() {
        return params;
    }
    
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    
    // 添加参数方法
    public void addParam(String key, Object value) {
        this.params.put(key, value);
    }
    
    @Override
    public String toString() {
        return "EmailMessage{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", templateId='" + templateId + '\'' +
                ", params=" + params +
                '}';
    }
} 