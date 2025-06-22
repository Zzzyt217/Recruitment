package com.test.Controller;

import com.test.Client.PositionClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class JobController {

    // 模拟一些职位数据，实际情况应该从数据库或远程服务获取
    private static final List<Map<String, Object>> JOBS = new ArrayList<>();

    @Resource
    private PositionClient positionClient;

    @GetMapping("/jobs")
    public String jobsPage(HttpServletRequest request, Model model) {
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            return "redirect:/auth/login";
        }

        try {
            // 验证用户ID
            Integer userId = Integer.parseInt(userIdStr);
            System.out.println("访问职位页面的用户ID: " + userId);
            
            // 尝试获取所有职位数据，预加载到模型中
            try {
                System.out.println("正在调用getAllPublishedPositions获取所有职位...");
                List<Map<String, Object>> allJobs = positionClient.getAllPublishedPositions(authHeader, userIdStr);
                System.out.println("预加载职位数量: " + (allJobs != null ? allJobs.size() : 0));
                
                if (allJobs != null && !allJobs.isEmpty()) {
                    model.addAttribute("preloadedJobs", allJobs);
                    model.addAttribute("hasPreloadedJobs", true);
                    System.out.println("成功预加载职位数据");
                } else {
                    System.out.println("获取到的职位数据为空");
                    model.addAttribute("hasPreloadedJobs", false);
                }
            } catch (Exception e) {
                System.err.println("预加载职位数据失败: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("hasPreloadedJobs", false);
            }
            
            return "jobs";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/api/jobs/search")
    @ResponseBody
    public List<Map<String, Object>> searchJobs(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "category", required = false) String category,
            HttpServletRequest request) {

        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");

        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            throw new RuntimeException("未授权访问");
        }

        try {
            // 验证用户ID
            Integer userId = Integer.parseInt(userIdStr);
            System.out.println("搜索职位的用户ID: " + userId);

            // 打印调试信息
            System.out.println("职位搜索 - 关键词: " + keyword);
            System.out.println("职位搜索 - 地点: " + location);
            System.out.println("职位搜索 - 类别: " + category);
            System.out.println("职位搜索 - Auth: " + (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "无"));

            // 使用Feign客户端从company服务获取职位数据
            List<Map<String, Object>> jobs;
            try {
                if (keyword == null || keyword.trim().isEmpty()) {
                    // 如果没有关键词，返回所有职位
                    System.out.println("调用getAllPublishedPositions获取所有职位");
                    jobs = positionClient.getAllPublishedPositions(authHeader, userIdStr);
                    System.out.println("获取所有职位数量: " + (jobs != null ? jobs.size() : 0));
                } else {
                    // 根据关键词搜索职位
                    System.out.println("调用searchPositions搜索职位");
                    jobs = positionClient.searchPositions(authHeader, userIdStr, keyword, location, category);
                    System.out.println("搜索职位数量: " + (jobs != null ? jobs.size() : 0));
                }
                
                return jobs != null ? jobs : Collections.emptyList();
            } catch (Exception e) {
                System.err.println("调用company服务失败: " + e.getMessage());
                e.printStackTrace();
                return Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("搜索职位时发生错误: " + e.getMessage());
            throw new RuntimeException("搜索职位时发生错误: " + e.getMessage());
        }
    }

}