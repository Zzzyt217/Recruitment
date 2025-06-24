package com.test.Pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 申请状态变更消息模型类
 */
public class ApplicationStatusChangeMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // 申请ID
    private Long applicationId;
    // 新状态
    private String newStatus;
    // 更新时间
    private Date updateTime;
    
    public ApplicationStatusChangeMessage() {
    }
    
    public ApplicationStatusChangeMessage(Long applicationId, String newStatus, Date updateTime) {
        this.applicationId = applicationId;
        this.newStatus = newStatus;
        this.updateTime = updateTime;
    }
    
    // Getters and Setters
    public Long getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    @Override
    public String toString() {
        return "ApplicationStatusChangeMessage{" +
                "applicationId=" + applicationId +
                ", newStatus='" + newStatus + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
} 