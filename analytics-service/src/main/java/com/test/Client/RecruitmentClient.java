package com.test.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Recruitment服务Feign客户端
 * 用于获取招聘相关统计数据
 */
@FeignClient(name = "recruitment-service")
public interface RecruitmentClient {
    
    /**
     * 获取成功招聘数量（录用状态的申请数）
     * @param authorization 认证令牌
     * @param userId 用户ID
     * @return 成功招聘数量
     */
    @GetMapping("/api/stats/successful-hire-count")
    Integer getSuccessfulHireCount(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("UserId") String userId);
} 