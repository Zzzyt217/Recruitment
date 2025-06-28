package com.test.Pojo;

import java.util.Date;

public class Application {
    private Long id;
    private Integer userId;
    private Long positionId;
    private Long companyId;
    private Long resumeId;
    private String coverLetter;
    private String status; // APPLIED, REVIEWING, INTERVIEW, OFFER, REJECTED, ACCEPTED
    private Date createdAt;
    private Date updatedAt;
    
    // 额外的非数据库字段，用于展示
    private String userName;
    private String positionTitle;
    private String positionLocation;
    private String companyName;
    private String resumeName;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Long getPositionId() {
        return positionId;
    }
    
    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }
    
    public Long getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
    
    public Long getResumeId() {
        return resumeId;
    }
    
    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }
    
    public String getCoverLetter() {
        return coverLetter;
    }
    
    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getUserName() {
        return userName != null ? userName : "";
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getPositionTitle() {
        return positionTitle != null ? positionTitle : "";
    }
    
    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }
    
    public String getPositionLocation() {
        return positionLocation != null ? positionLocation : "";
    }
    
    public void setPositionLocation(String positionLocation) {
        this.positionLocation = positionLocation;
    }
    
    public String getCompanyName() {
        return companyName != null ? companyName : "";
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getResumeName() {
        return resumeName != null ? resumeName : "";
    }
    
    public void setResumeName(String resumeName) {
        this.resumeName = resumeName;
    }
} 