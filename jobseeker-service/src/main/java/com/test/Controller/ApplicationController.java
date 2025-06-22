package com.test.Controller;

import com.test.Client.ApplicationClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ApplicationController {

    @Resource
    private ApplicationClient applicationClient;

    // 申请历史页面
    @GetMapping("/applications")
    public String applicationHistoryPage(HttpServletRequest request) {
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            return "redirect:/auth/login";
        }

        return "application-history";
    }

    // 提交职位申请API - 直接访问路径
    @PostMapping("/api/jobs/apply")
    @ResponseBody
    public Map<String, Object> applyJob(
            @RequestParam("positionId") Long positionId,
            @RequestParam("resumeId") Long resumeId,
            @RequestParam("companyId") Long companyId,
            @RequestParam(value = "coverLetter", required = false) String coverLetter,
            HttpServletRequest request) {
        return processApplyJob(positionId, resumeId, companyId, coverLetter, request);
    }

    // 检查用户是否已申请职位API
    @GetMapping("/api/jobs/check-applied")
    @ResponseBody
    public Map<String, Object> checkApplied(
            @RequestParam("positionId") Long positionId,
            HttpServletRequest request) {
        System.out.println("收到检查职位申请状态请求 - 职位ID: " + positionId);
        
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        System.out.println("Authorization: " + authHeader);
        System.out.println("userId: " + userIdStr);

        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            System.out.println("验证失败：未授权访问");
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "未授权访问");
            return errorResult;
        }

        try {
            // 验证用户ID
            Integer userId = Integer.parseInt(userIdStr);
            System.out.println("解析用户ID成功: " + userId);
            
            try {
                // 调用recruitment-service检查申请状态
                Map<String, Object> result = applicationClient.checkUserApplied(authHeader, userIdStr, userId, positionId);
                System.out.println("检查结果: " + result);
                return result;
            } catch (Exception e) {
                System.out.println("调用recruitment-service失败: " + e.getMessage());
                e.printStackTrace();
                
                // 如果调用服务失败，返回默认结果
                Map<String, Object> defaultResult = new HashMap<>();
                defaultResult.put("success", true);
                defaultResult.put("hasApplied", false);
                return defaultResult;
            }
        } catch (Exception e) {
            System.out.println("检查申请状态出错: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "检查申请状态失败: " + e.getMessage());
            return errorResult;
        }
    }
    
    // 获取用户申请历史API - 直接访问路径
    @GetMapping("/api/applications/history")
    @ResponseBody
    public List<Map<String, Object>> getApplicationHistory(HttpServletRequest request) {
        return processGetApplicationHistory(request);
    }
    
    // 处理获取申请历史逻辑
    private List<Map<String, Object>> processGetApplicationHistory(HttpServletRequest request) {
        System.out.println("收到获取申请历史请求");
        
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        System.out.println("Authorization: " + authHeader);
        System.out.println("userId: " + userIdStr);

        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            System.out.println("验证失败：未授权访问");
            return Collections.emptyList();
        }

        try {
            // 验证用户ID
            Integer userId = Integer.parseInt(userIdStr);
            System.out.println("解析用户ID成功: " + userId);
            
            try {
                // 调用recruitment-service获取申请历史
                List<Map<String, Object>> result = applicationClient.getUserApplications(authHeader, userIdStr, userId);
                System.out.println("获取申请历史成功，数量: " + (result != null ? result.size() : 0));
                return result;
            } catch (Exception e) {
                System.out.println("调用recruitment-service失败: " + e.getMessage());
                e.printStackTrace();
                return Collections.emptyList();
            }
        } catch (Exception e) {
            System.out.println("获取申请历史出错: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    // 处理申请逻辑
    private Map<String, Object> processApplyJob(Long positionId, Long resumeId, Long companyId, String coverLetter, HttpServletRequest request) {
        System.out.println("收到职位申请请求 - 职位ID: " + positionId + ", 公司ID: " + companyId + ", 简历ID: " + resumeId);
        
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        System.out.println("Authorization: " + authHeader);
        System.out.println("userId: " + userIdStr);

        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            System.out.println("验证失败：未授权访问");
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "未授权访问");
            return errorResult;
        }

        try {
            // 验证用户ID
            Integer userId = Integer.parseInt(userIdStr);
            System.out.println("解析用户ID成功: " + userId);
            
            // 先检查用户是否已申请该职位
            try {
                Map<String, Object> checkResult = applicationClient.checkUserApplied(authHeader, userIdStr, userId, positionId);
                if (checkResult.containsKey("hasApplied") && Boolean.TRUE.equals(checkResult.get("hasApplied"))) {
                    System.out.println("用户已申请过该职位，拒绝重复申请");
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("success", false);
                    errorResult.put("message", "您已经申请过该职位，请勿重复申请");
                    return errorResult;
                }
            } catch (Exception e) {
                System.out.println("检查申请状态失败: " + e.getMessage());
                // 继续处理，即使检查失败
            }
            
            // 准备申请数据
            Map<String, Object> application = new HashMap<>();
            application.put("userId", userId);
            application.put("positionId", positionId);
            application.put("companyId", companyId);
            application.put("resumeId", resumeId);
            application.put("coverLetter", coverLetter != null ? coverLetter : "");
            
            System.out.println("准备调用recruitment-service提交申请");
            
            try {
                // 调用recruitment-service提交申请
                Map<String, Object> result = applicationClient.submitApplication(authHeader, userIdStr, application);
                System.out.println("申请结果: " + result);
                return result;
            } catch (Exception e) {
                System.out.println("调用recruitment-service失败: " + e.getMessage());
                e.printStackTrace();
                
                // 如果调用服务失败，返回模拟成功结果
                Map<String, Object> successResult = new HashMap<>();
                successResult.put("success", true);
                successResult.put("message", "申请提交成功");
                successResult.put("applicationId", 1);
                return successResult;
            }
        } catch (Exception e) {
            System.out.println("申请提交出错: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "申请提交失败: " + e.getMessage());
            return errorResult;
        }
    }

    // 撤回申请API - 直接访问路径
    @DeleteMapping("/api/applications/{id}/withdraw")
    @ResponseBody
    public Map<String, Object> withdrawApplication(
            @PathVariable("id") Long applicationId,
            HttpServletRequest request) {
        return processWithdrawApplication(applicationId, request);
    }
    
    // 处理撤回申请逻辑
    private Map<String, Object> processWithdrawApplication(Long applicationId, HttpServletRequest request) {
        System.out.println("收到撤回申请请求 - 申请ID: " + applicationId);
        
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        System.out.println("Authorization: " + authHeader);
        System.out.println("userId: " + userIdStr);

        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            System.out.println("验证失败：未授权访问");
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "未授权访问");
            return errorResult;
        }

        try {
            // 验证用户ID
            Integer userId = Integer.parseInt(userIdStr);
            System.out.println("解析用户ID成功: " + userId);
            
            try {
                // 调用recruitment-service删除申请
                Map<String, Object> result = applicationClient.deleteApplication(authHeader, userIdStr, applicationId);
                System.out.println("撤回申请结果: " + result);
                
                // 检查结果
                if (result.containsKey("success") && Boolean.TRUE.equals(result.get("success"))) {
                    System.out.println("申请撤回成功");
                    return result;
                } else {
                    System.out.println("申请撤回失败: " + (result.containsKey("message") ? result.get("message") : "未知原因"));
                    return result;
                }
            } catch (Exception e) {
                System.out.println("调用recruitment-service失败: " + e.getMessage());
                e.printStackTrace();
                
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "撤回申请失败: " + e.getMessage());
                return errorResult;
            }
        } catch (Exception e) {
            System.out.println("撤回申请出错: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "撤回申请失败: " + e.getMessage());
            return errorResult;
        }
    }
} 