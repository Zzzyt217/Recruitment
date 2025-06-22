package com.test.Pojo;

import java.util.Date;

public class Interview {
    private Long id;
    private Long applicationId;
    private Date interviewTime;
    private String location;
    private String interviewer;
    private String feedback;
    private String status; // SCHEDULED, COMPLETED, CANCELLED
    
    // 额外的非数据库字段
    private String candidateName;
    private String positionTitle;
    private String companyName;
    private Application application;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
    
    public Date getInterviewTime() {
        return interviewTime;
    }
    
    public void setInterviewTime(Date interviewTime) {
        this.interviewTime = interviewTime;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getInterviewer() {
        return interviewer;
    }
    
    public void setInterviewer(String interviewer) {
        this.interviewer = interviewer;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCandidateName() {
        return candidateName;
    }
    
    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    
    public String getPositionTitle() {
        return positionTitle;
    }
    
    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public Application getApplication() {
        return application;
    }
    
    public void setApplication(Application application) {
        this.application = application;
    }
} 