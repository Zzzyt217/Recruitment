package com.test.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class JobController {

    // 模拟一些职位数据，实际情况应该从数据库或远程服务获取
    private static final List<Map<String, Object>> JOBS = new ArrayList<>();

    static {
        // 初始化一些示例职位数据
        initJobData();
    }
    
    @GetMapping("/jobs")
    public String jobsPage(HttpServletRequest request) {
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
            System.out.println("职位搜索 - Auth: " + (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "无"));
            System.out.println("职位搜索 - UserId: " + userId);

            if (keyword == null || keyword.trim().isEmpty()) {
                // 如果没有关键词，返回所有职位
                return JOBS;
            }

            // 简单的关键词过滤逻辑
            String lowerKeyword = keyword.toLowerCase();

            List<Map<String, Object>> filteredJobs = JOBS.stream()
                .filter(job -> {
                    String title = (String) job.get("title");
                    String company = (String) job.get("company");
                    String description = (String) job.get("description");
                    List<String> tags = (List<String>) job.get("tags");

                    // 搜索职位名称、公司名称、描述和标签
                    boolean matchTitle = title != null && title.toLowerCase().contains(lowerKeyword);
                    boolean matchCompany = company != null && company.toLowerCase().contains(lowerKeyword);
                    boolean matchDescription = description != null && description.toLowerCase().contains(lowerKeyword);

                    boolean matchTags = false;
                    if (tags != null) {
                        matchTags = tags.stream()
                            .anyMatch(tag -> tag.toLowerCase().contains(lowerKeyword));
                    }

                    return matchTitle || matchCompany || matchDescription || matchTags;
                })
                .collect(Collectors.toList());

            return filteredJobs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("搜索职位时发生错误");
        }
    }

    // 初始化示例职位数据
    private static void initJobData() {
        // 职位1
        Map<String, Object> job1 = new HashMap<>();
        job1.put("id", 1);
        job1.put("title", "高级Java开发工程师");
        job1.put("company", "阿里巴巴科技有限公司");
        job1.put("location", "杭州");
        job1.put("salary", "25k-35k");
        job1.put("description", "负责电商平台后端系统开发，优化现有系统架构，参与技术方案设计与评审。");
        job1.put("tags", Arrays.asList("Java", "Spring", "微服务", "高并发"));

        // 职位2
        Map<String, Object> job2 = new HashMap<>();
        job2.put("id", 2);
        job2.put("title", "前端开发工程师");
        job2.put("company", "腾讯科技有限公司");
        job2.put("location", "深圳");
        job2.put("salary", "20k-30k");
        job2.put("description", "负责企业级应用前端开发，优化用户体验，提升页面性能。");
        job2.put("tags", Arrays.asList("JavaScript", "Vue", "React", "前端优化"));

        // 职位3
        Map<String, Object> job3 = new HashMap<>();
        job3.put("id", 3);
        job3.put("title", "人工智能算法工程师");
        job3.put("company", "字节跳动");
        job3.put("location", "北京");
        job3.put("salary", "30k-50k");
        job3.put("description", "负责推荐系统算法开发与优化，提升产品推荐准确率。");
        job3.put("tags", Arrays.asList("Python", "机器学习", "深度学习", "推荐系统"));

        // 职位4
        Map<String, Object> job4 = new HashMap<>();
        job4.put("id", 4);
        job4.put("title", "产品经理");
        job4.put("company", "美团点评");
        job4.put("location", "上海");
        job4.put("salary", "18k-25k");
        job4.put("description", "负责产品规划、需求分析、产品设计和迭代优化。");
        job4.put("tags", Arrays.asList("需求分析", "产品设计", "用户体验", "数据分析"));

        // 职位5
        Map<String, Object> job5 = new HashMap<>();
        job5.put("id", 5);
        job5.put("title", "DevOps工程师");
        job5.put("company", "华为技术有限公司");
        job5.put("location", "深圳");
        job5.put("salary", "20k-30k");
        job5.put("description", "负责构建与维护CI/CD流程，提升团队开发效率，保障系统稳定性。");
        job5.put("tags", Arrays.asList("Docker", "Kubernetes", "Jenkins", "自动化部署"));

        // 职位6
        Map<String, Object> job6 = new HashMap<>();
        job6.put("id", 6);
        job6.put("title", "数据分析师");
        job6.put("company", "网易游戏");
        job6.put("location", "广州");
        job6.put("salary", "15k-25k");
        job6.put("description", "负责游戏数据分析，提供决策支持，优化游戏体验。");
        job6.put("tags", Arrays.asList("SQL", "Python", "数据可视化", "用户行为分析"));

        // 职位7
        Map<String, Object> job7 = new HashMap<>();
        job7.put("id", 7);
        job7.put("title", "安卓开发工程师");
        job7.put("company", "小米科技");
        job7.put("location", "北京");
        job7.put("salary", "18k-28k");
        job7.put("description", "负责小米系列App开发与优化，提升用户体验和应用性能。");
        job7.put("tags", Arrays.asList("Android", "Kotlin", "Java", "移动开发"));

        // 职位8
        Map<String, Object> job8 = new HashMap<>();
        job8.put("id", 8);
        job8.put("title", "iOS开发工程师");
        job8.put("company", "蚂蚁金服");
        job8.put("location", "杭州");
        job8.put("salary", "20k-35k");
        job8.put("description", "负责支付宝App功能开发与性能优化，提升用户体验。");
        job8.put("tags", Arrays.asList("iOS", "Swift", "Objective-C", "移动支付"));

        // 职位9
        Map<String, Object> job9 = new HashMap<>();
        job9.put("id", 9);
        job9.put("title", "UI设计师");
        job9.put("company", "京东");
        job9.put("location", "北京");
        job9.put("salary", "15k-25k");
        job9.put("description", "负责电商平台UI设计，打造极致用户体验。");
        job9.put("tags", Arrays.asList("UI设计", "Figma", "Adobe XD", "用户体验"));

        // 职位10
        Map<String, Object> job10 = new HashMap<>();
        job10.put("id", 10);
        job10.put("title", "测试工程师");
        job10.put("company", "百度");
        job10.put("location", "北京");
        job10.put("salary", "15k-22k");
        job10.put("description", "负责产品质量保障，包括功能测试、性能测试和自动化测试。");
        job10.put("tags", Arrays.asList("自动化测试", "性能测试", "测试用例设计", "质量保障"));

        // 添加到职位列表
        JOBS.add(job1);
        JOBS.add(job2);
        JOBS.add(job3);
        JOBS.add(job4);
        JOBS.add(job5);
        JOBS.add(job6);
        JOBS.add(job7);
        JOBS.add(job8);
        JOBS.add(job9);
        JOBS.add(job10);
    }
}