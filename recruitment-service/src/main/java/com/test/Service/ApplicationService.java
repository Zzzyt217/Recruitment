package com.test.Service;

import com.test.Pojo.Application;
import java.util.List;
import java.util.Map;

public interface ApplicationService {
    // 创建申请
    Map<String, Object> createApplication(Application application);
    
    // 根据ID获取申请
    Map<String, Object> getApplicationById(Long id);
    
    // 获取用户的所有申请
    List<Map<String, Object>> getApplicationsByUserId(Integer userId);
    
    // 获取公司的所有申请
    List<Map<String, Object>> getApplicationsByCompanyId(Long companyId);
    
    // 获取职位的所有申请
    List<Map<String, Object>> getApplicationsByPositionId(Long positionId);
    
    // 更新申请状态
    Map<String, Object> updateApplicationStatus(Long id, String status);
    
    // 删除申请
    boolean deleteApplication(Long id);
    
    // 检查用户是否已申请职位
    boolean hasUserApplied(Integer userId, Long positionId);
} 