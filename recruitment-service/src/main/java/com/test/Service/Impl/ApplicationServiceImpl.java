package com.test.Service.Impl;

import com.test.Mapper.ApplicationMapper;
import com.test.Pojo.Application;
import com.test.Service.ApplicationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    
    @Resource
    private ApplicationMapper applicationMapper;
    
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
                
                // 添加用户名和职位标题（如果有）
                if (application.getUserName() != null) {
                    appMap.put("userName", application.getUserName());
                }
                if (application.getPositionTitle() != null) {
                    appMap.put("positionTitle", application.getPositionTitle());
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
            Date now = new Date();
            int updateResult = applicationMapper.updateStatus(id, status, now);
            
            if (updateResult > 0) {
                result.put("success", true);
                result.put("message", "申请状态更新成功");
            } else {
                result.put("success", false);
                result.put("message", "申请状态更新失败");
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