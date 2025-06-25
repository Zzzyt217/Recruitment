package com.test.Controller;

import com.test.Mapper.ResumeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 统计数据控制器
 * 提供与求职者相关的统计数据API
 */
@Slf4j
@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Resource
    private ResumeMapper resumeMapper;

    /**
     * 获取求职者总数
     * 
     * @param authorization 认证令牌
     * @param userId 用户ID
     * @return 求职者总数
     */
    @GetMapping("/jobseeker-count")
    public Integer getJobseekerCount(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("UserId") String userId) {
        System.out.println("接收到获取求职者数量请求, userId: " + userId);
        System.out.println("授权信息长度: " + (authorization != null ? authorization.length() : "null"));
        
        try {
            System.out.println("执行SQL: SELECT COUNT(*) FROM resume");
            Integer count = resumeMapper.countAll();
            System.out.println("SQL执行结果: count = " + count);
            return count;
        } catch (Exception e) {
            System.out.println("获取求职者数量失败: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
} 