package com.test.Service;

import com.test.Pojo.DashboardStatsDTO;

/**
 * 数据分析服务接口
 */
public interface AnalyticsService {
    
    /**
     * 获取仪表盘统计数据
     * @param token 认证令牌
     * @param userId 用户ID
     * @return 仪表盘统计数据
     */
    DashboardStatsDTO getDashboardStats(String token, String userId);
} 