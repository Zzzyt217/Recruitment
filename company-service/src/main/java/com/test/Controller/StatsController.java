package com.test.Controller;

import com.test.Mapper.CompanyMapper;
import com.test.Mapper.PositionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 统计数据控制器
 * 提供与企业和职位相关的统计数据API
 */
@Slf4j
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Resource
    private CompanyMapper companyMapper;
    
    @Resource
    private PositionMapper positionMapper;
    
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取企业总数
     * 
     * @param authorization 认证令牌
     * @param userId 用户ID
     * @return 企业总数
     */
    @GetMapping("/company-count")
    public Integer getCompanyCount(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("UserId") String userId) {
        System.out.println("接收到获取企业数量请求, userId: " + userId);
        System.out.println("授权信息长度: " + (authorization != null ? authorization.length() : "null"));
        
        try {
            System.out.println("执行SQL: SELECT COUNT(*) FROM company");
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM company", Integer.class);
            System.out.println("SQL执行结果: count = " + count);
            return count != null ? count : 0;
        } catch (Exception e) {
            System.out.println("获取企业数量失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * 获取职位总数
     * 
     * @param authorization 认证令牌
     * @param userId 用户ID
     * @return 职位总数
     */
    @GetMapping("/position-count")
    public Integer getPositionCount(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("UserId") String userId) {
        System.out.println("接收到获取职位数量请求, userId: " + userId);
        System.out.println("授权信息长度: " + (authorization != null ? authorization.length() : "null"));
        
        try {
            System.out.println("执行SQL: SELECT COUNT(*) FROM position WHERE status = 'PUBLISHED'");
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM position WHERE status = 'PUBLISHED'", Integer.class);
            System.out.println("SQL执行结果: count = " + count);
            return count != null ? count : 0;
        } catch (Exception e) {
            System.out.println("获取职位数量失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
} 