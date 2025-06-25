package com.test.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Jobseeker服务Feign客户端
 * 用于获取求职者相关统计数据
 */
@FeignClient(name = "jobseeker-service")
public interface JobseekerClient {
    
    /**
     * 获取求职者总数
     * @param authorization 认证令牌
     * @param userId 用户ID
     * @return 求职者总数
     */
    @GetMapping("/api/stats/jobseeker-count")
    Integer getJobseekerCount(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("UserId") String userId);
} 