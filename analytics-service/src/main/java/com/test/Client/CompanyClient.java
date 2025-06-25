package com.test.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Company服务Feign客户端
 * 用于获取公司和职位相关统计数据
 */
@FeignClient(name = "company-service")
public interface CompanyClient {
    
    /**
     * 获取企业总数
     * @param authorization 认证令牌
     * @param userId 用户ID
     * @return 企业总数
     */
    @GetMapping("/api/stats/company-count")
    Integer getCompanyCount(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("UserId") String userId);
    
    /**
     * 获取职位总数
     * @param authorization 认证令牌
     * @param userId 用户ID
     * @return 职位总数
     */
    @GetMapping("/api/stats/position-count")
    Integer getPositionCount(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("UserId") String userId);
} 