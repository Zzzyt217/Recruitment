package com.test.Controller;

import com.test.Pojo.DashboardStatsDTO;
import com.test.Service.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据分析控制器
 * 提供统计数据的API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * 获取仪表盘统计数据
     * @param authorization 认证令牌
     * @param userId 用户ID
     * @return 仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public DashboardStatsDTO getDashboardStats(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("UserId") String userId) {
        
        System.out.println("==============================================");
        System.out.println("AnalyticsController接收到仪表盘统计数据请求");
        System.out.println("userId: " + userId);
        System.out.println("authorization长度: " + (authorization != null ? authorization.length() : "null"));
        
        try {
            DashboardStatsDTO stats = analyticsService.getDashboardStats(authorization, userId);
            System.out.println("获取统计数据成功: " + stats);
            return stats;
        } catch (Exception e) {
            System.out.println("获取仪表盘统计数据失败: " + e.getMessage());
            e.printStackTrace();
            return new DashboardStatsDTO();
        }
    }
} 