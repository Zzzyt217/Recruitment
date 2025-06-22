package com.test.Service;

import com.test.Pojo.Interview;
import java.util.List;
import java.util.Map;

public interface InterviewService {
    // 创建面试
    Map<String, Object> createInterview(Interview interview);
    
    // 根据ID获取面试
    Map<String, Object> getInterviewById(Long id);
    
    // 获取申请的所有面试
    List<Map<String, Object>> getInterviewsByApplicationId(Long applicationId);
    
    // 更新面试信息
    Map<String, Object> updateInterview(Interview interview);
    
    // 更新面试反馈
    Map<String, Object> updateInterviewFeedback(Long id, String feedback);
    
    // 删除面试
    boolean deleteInterview(Long id);
} 