package com.test.Controller;

import com.test.Mapper.ApplicationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 统计数据控制器
 * 提供与招聘相关的统计数据API
 */
@Slf4j
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Resource
    private ApplicationMapper applicationMapper;
    
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取成功招聘数量（状态为OFFER的申请数量）
     * 
     * @param authorization 认证令牌
     * @param userId 用户ID
     * @return 成功招聘数量
     */
    @GetMapping("/successful-hire-count")
    public Integer getSuccessfulHireCount(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("UserId") String userId) {
        System.out.println("接收到获取成功招聘数量请求, userId: " + userId);
        System.out.println("授权信息长度: " + (authorization != null ? authorization.length() : "null"));
        
        try {
            System.out.println("执行SQL: SELECT COUNT(*) FROM application WHERE status = 'OFFER'");
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM application WHERE status = 'OFFER'", 
                Integer.class
            );
            System.out.println("SQL执行结果: count = " + count);
            return count != null ? count : 0;
        } catch (Exception e) {
            System.out.println("获取成功招聘数量失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
} 