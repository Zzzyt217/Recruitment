package com.test.Service.Impl;

import com.test.Mapper.ApplicationMapper;
import com.test.Pojo.Application;
import com.test.Service.ApplicationService;
import com.test.Service.EmailService;
import com.test.Service.Impl.EmailMessageProducer;
import com.test.Service.Impl.ApplicationStatusProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    
    @Resource
    private ApplicationMapper applicationMapper;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private EmailMessageProducer emailMessageProducer;
    
    @Autowired
    private ApplicationStatusProducer applicationStatusProducer;
    
    @Override
    public Map<String, Object> createApplication(Application application) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查用户是否已经申请过该职位
            if (hasUserApplied(application.getUserId(), application.getPositionId())) {
                result.put("success", false);
                result.put("message", "您已经申请过该职位，请勿重复申请");
                return result;
            }
            
            // 设置初始状态和时间
            application.setStatus("APPLIED");
            Date now = new Date();
            application.setCreatedAt(now);
            application.setUpdatedAt(now);
            
            // 插入数据库
            int insertResult = applicationMapper.insert(application);
            
            if (insertResult > 0) {
                result.put("success", true);
                result.put("message", "申请提交成功");
                result.put("applicationId", application.getId());
            } else {
                result.put("success", false);
                result.put("message", "申请提交失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "申请提交失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> getApplicationById(Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Application application = applicationMapper.findById(id);
            
            if (application != null) {
                result.put("success", true);
                result.put("application", application);
            } else {
                result.put("success", false);
                result.put("message", "未找到该申请记录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取申请记录失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getApplicationsByUserId(Integer userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            List<Application> applications = applicationMapper.findByUserId(userId);
            
            for (Application application : applications) {
                Map<String, Object> appMap = new HashMap<>();
                appMap.put("id", application.getId());
                appMap.put("userId", application.getUserId());
                appMap.put("positionId", application.getPositionId());
                appMap.put("companyId", application.getCompanyId());
                appMap.put("resumeId", application.getResumeId());
                appMap.put("status", application.getStatus());
                
                // 处理可能为null的日期
                if (application.getCreatedAt() != null) {
                    appMap.put("createdAt", application.getCreatedAt());
                } else {
                    // 如果创建日期为null，使用当前日期作为默认值
                    appMap.put("createdAt", new Date());
                }
                
                if (application.getUpdatedAt() != null) {
                    appMap.put("updatedAt", application.getUpdatedAt());
                } else {
                    // 如果更新日期为null，使用当前日期作为默认值
                    appMap.put("updatedAt", new Date());
                }
                
                // 处理职位标题和公司名称
                if (application.getPositionTitle() != null && !application.getPositionTitle().isEmpty()) {
                    appMap.put("positionTitle", application.getPositionTitle());
                } else {
                    // 如果职位标题为null，使用默认值
                    appMap.put("positionTitle", "职位 #" + application.getPositionId());
                }
                
                // 处理职位地点
                if (application.getPositionLocation() != null && !application.getPositionLocation().isEmpty()) {
                    appMap.put("positionLocation", application.getPositionLocation());
                } else {
                    // 如果职位地点为null，使用默认值
                    appMap.put("positionLocation", "未知地点");
                }
                
                if (application.getCompanyName() != null && !application.getCompanyName().isEmpty()) {
                    appMap.put("companyName", application.getCompanyName());
                } else {
                    // 如果公司名称为null，使用默认值
                    appMap.put("companyName", "公司 #" + application.getCompanyId());
                }
                
                result.add(appMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getApplicationsByCompanyId(Long companyId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            List<Application> applications = applicationMapper.findByCompanyId(companyId);
            
            for (Application application : applications) {
                Map<String, Object> appMap = new HashMap<>();
                appMap.put("id", application.getId());
                appMap.put("userId", application.getUserId());
                appMap.put("positionId", application.getPositionId());
                appMap.put("companyId", application.getCompanyId());
                appMap.put("resumeId", application.getResumeId());
                appMap.put("status", application.getStatus());
                appMap.put("createdAt", application.getCreatedAt());
                appMap.put("updatedAt", application.getUpdatedAt());
                
                // 手动查询用户名和职位标题
                try (Connection conn = dataSource.getConnection()) {
                    // 查询用户姓名（从jobseekers库的resume表获取）
                    PreparedStatement userStmt = conn.prepareStatement(
                        "SELECT name FROM jobseekers.resume WHERE user_id = ?"
                    );
                    userStmt.setInt(1, application.getUserId());
                    ResultSet userRs = userStmt.executeQuery();
                    if (userRs.next()) {
                        String userName = userRs.getString("name");
                        appMap.put("userName", userName);
                    } else {
                        // 如果resume表中没有，尝试从users表获取用户名作为备选
                        PreparedStatement backupStmt = conn.prepareStatement(
                            "SELECT username FROM recruitment.users WHERE id = ?"
                        );
                        backupStmt.setInt(1, application.getUserId());
                        ResultSet backupRs = backupStmt.executeQuery();
                        if (backupRs.next()) {
                            String userName = backupRs.getString("username");
                            appMap.put("userName", userName);
                        }
                    }
                    
                    // 查询职位标题
                    PreparedStatement posStmt = conn.prepareStatement(
                        "SELECT title FROM company1_db.position WHERE id = ?"
                    );
                    posStmt.setLong(1, application.getPositionId());
                    ResultSet posRs = posStmt.executeQuery();
                    if (posRs.next()) {
                        String positionTitle = posRs.getString("title");
                        appMap.put("positionTitle", positionTitle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                result.add(appMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getApplicationsByPositionId(Long positionId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            List<Application> applications = applicationMapper.findByPositionId(positionId);
            
            for (Application application : applications) {
                Map<String, Object> appMap = new HashMap<>();
                appMap.put("id", application.getId());
                appMap.put("userId", application.getUserId());
                appMap.put("positionId", application.getPositionId());
                appMap.put("companyId", application.getCompanyId());
                appMap.put("resumeId", application.getResumeId());
                appMap.put("status", application.getStatus());
                appMap.put("createdAt", application.getCreatedAt());
                appMap.put("updatedAt", application.getUpdatedAt());
                
                // 添加用户名（如果有）
                if (application.getUserName() != null) {
                    appMap.put("userName", application.getUserName());
                }
                
                result.add(appMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> updateApplicationStatus(Long id, String status) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证状态值是否有效
            Set<String> validStatuses = new HashSet<>(Arrays.asList(
                "PENDING", "APPLIED", "REVIEWING", "INTERVIEW", "ACCEPTED", "OFFER", "REJECTED"
            ));
            
            if (!validStatuses.contains(status.toUpperCase())) {
                result.put("success", false);
                result.put("message", "无效的状态值：" + status);
                return result;
            }
            
            // 统一状态为大写
            String upperStatus = status.toUpperCase();
            
            // 更新状态
            Date now = new Date();
            int updateResult = applicationMapper.updateStatus(id, upperStatus, now);
            
            if (updateResult > 0) {
                // 发送申请状态变更消息
                applicationStatusProducer.sendStatusChangeMessage(id, upperStatus);
                
                // 如果状态更新为OFFER，发送录用通知邮件
                if ("OFFER".equals(upperStatus)) {
                    try {
                        // 直接通过SQL查询获取所有需要的信息
                        try (Connection conn = dataSource.getConnection()) {
                            if (conn != null) {
                                // 一次性查询所有需要的信息，包括resume中的姓名
                                PreparedStatement appStmt = conn.prepareStatement(
                                    "SELECT a.*, u.eemail, u.username, r.name as resume_name, p.title as position_title, c.name as company_name " +
                                    "FROM recruitment_db.application a " +
                                    "LEFT JOIN recruitment.users u ON a.user_id = u.id " +
                                    "LEFT JOIN jobseekers.resume r ON u.id = r.user_id " +
                                    "LEFT JOIN company1_db.position p ON a.position_id = p.id " +
                                    "LEFT JOIN company1_db.company c ON a.company_id = c.id " +
                                    "WHERE a.id = ?"
                                );
                                appStmt.setLong(1, id);
                                ResultSet appRs = appStmt.executeQuery();
                                
                                if (appRs.next()) {
                                    // 获取所需的信息
                                    String userEmail = appRs.getString("eemail");
                                    
                                    // 优先使用简历中的姓名，如果为空则使用用户名
                                    String resumeName = appRs.getString("resume_name");
                                    String username = appRs.getString("username");
                                    String candidateName = (resumeName != null && !resumeName.isEmpty()) ? resumeName : username;
                                    
                                    String positionTitle = appRs.getString("position_title");
                                    String companyName = appRs.getString("company_name");
                                    
                                    // 使用消息队列发送录用通知邮件
                                    if (userEmail != null && !userEmail.isEmpty()) {
                                        emailMessageProducer.sendOfferNotificationMessage(
                                            userEmail,
                                            candidateName != null ? candidateName : "求职者",
                                            positionTitle != null ? positionTitle : "相关职位",
                                            companyName != null ? companyName : "招聘企业"
                                        );
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 记录异常但不影响主要流程
                        e.printStackTrace();
                    }
                } else if ("INTERVIEW".equals(upperStatus)) {
                    // 如果状态更新为INTERVIEW，发送面试邀请
                    try {
                        try (Connection conn = dataSource.getConnection()) {
                            if (conn != null) {
                                PreparedStatement appStmt = conn.prepareStatement(
                                    "SELECT a.*, u.eemail, u.username, r.name as resume_name, p.title as position_title, c.name as company_name " +
                                    "FROM recruitment_db.application a " +
                                    "LEFT JOIN recruitment.users u ON a.user_id = u.id " +
                                    "LEFT JOIN jobseekers.resume r ON u.id = r.user_id " +
                                    "LEFT JOIN company1_db.position p ON a.position_id = p.id " +
                                    "LEFT JOIN company1_db.company c ON a.company_id = c.id " +
                                    "WHERE a.id = ?"
                                );
                                appStmt.setLong(1, id);
                                ResultSet appRs = appStmt.executeQuery();
                                
                                if (appRs.next()) {
                                    String userEmail = appRs.getString("eemail");
                                    String resumeName = appRs.getString("resume_name");
                                    String username = appRs.getString("username");
                                    String candidateName = (resumeName != null && !resumeName.isEmpty()) ? resumeName : username;
                                    String positionTitle = appRs.getString("position_title");
                                    String companyName = appRs.getString("company_name");
                                    
                                    // 使用消息队列发送面试邀请邮件
                                    if (userEmail != null && !userEmail.isEmpty()) {
                                        // 设置默认面试时间为3天后
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.add(Calendar.DAY_OF_MONTH, 3);
                                        Date interviewTime = calendar.getTime();
                                        
                                        emailMessageProducer.sendInterviewInvitationMessage(
                                            userEmail,
                                            candidateName != null ? candidateName : "求职者",
                                            positionTitle != null ? positionTitle : "相关职位",
                                            companyName != null ? companyName : "招聘企业",
                                            interviewTime,
                                            "公司总部"
                                        );
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                
                result.put("success", true);
                result.put("message", "申请状态更新成功");
            } else {
                result.put("success", false);
                result.put("message", "申请状态更新失败，申请ID可能不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "申请状态更新失败: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public boolean deleteApplication(Long id) {
        try {
            return applicationMapper.deleteById(id) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean hasUserApplied(Integer userId, Long positionId) {
        try {
            int count = applicationMapper.checkUserApplied(userId, positionId);
            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 