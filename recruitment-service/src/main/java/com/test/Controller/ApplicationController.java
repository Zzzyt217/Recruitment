package com.test.Controller;

import com.test.Pojo.Application;
import com.test.Service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin
public class ApplicationController {

    @Resource
    private ApplicationService applicationService;

    // 提交职位申请
    @PostMapping
    public Map<String, Object> submitApplication(@RequestBody Application application, HttpServletRequest request) {
        try {
            // 验证token
            String authHeader = request.getHeader("Authorization");
            String userIdStr = request.getHeader("userId");

            if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "未授权访问");
                return errorResult;
            }

            // 验证必填字段
            if (application.getUserId() == null || application.getPositionId() == null ||
                application.getCompanyId() == null || application.getResumeId() == null) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "缺少必要信息");
                return errorResult;
            }

            return applicationService.createApplication(application);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "申请提交失败: " + e.getMessage());
            return errorResult;
        }
    }

    // 检查用户是否已申请职位
    @GetMapping("/check")
    public Map<String, Object> checkUserApplied(
            @RequestParam("userId") Integer userId,
            @RequestParam("positionId") Long positionId,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 验证token
            String authHeader = request.getHeader("Authorization");
            String userIdStr = request.getHeader("userId");

            if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
                result.put("success", false);
                result.put("message", "未授权访问");
                return result;
            }

            boolean hasApplied = applicationService.hasUserApplied(userId, positionId);
            result.put("success", true);
            result.put("hasApplied", hasApplied);
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "检查失败: " + e.getMessage());
            return result;
        }
    }

    // 获取申请详情
    @GetMapping("/{id}")
    public Map<String, Object> getApplication(@PathVariable("id") Long id, HttpServletRequest request) {
        try {
            // 验证token
            String authHeader = request.getHeader("Authorization");
            String userIdStr = request.getHeader("userId");

            if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "未授权访问");
                return errorResult;
            }

            return applicationService.getApplicationById(id);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "获取申请失败: " + e.getMessage());
            return errorResult;
        }
    }

    // 获取用户的所有申请
    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getUserApplications(@PathVariable("userId") Integer userId, HttpServletRequest request) {
        try {
            // 验证token
            String authHeader = request.getHeader("Authorization");
            String userIdStr = request.getHeader("userId");

            if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
                return Collections.emptyList();
            }

            return applicationService.getApplicationsByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 获取公司的所有申请
    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getCompanyApplications(@PathVariable("companyId") Long companyId, HttpServletRequest request) {
        try {
            // 验证token
            String authHeader = request.getHeader("Authorization");
            String userIdStr = request.getHeader("userId");

            if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
                System.out.println("\n身份验证失败 - 缺少必要的认证信息");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "未授权访问");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            List<Map<String, Object>> applications = applicationService.getApplicationsByCompanyId(companyId);
            
            System.out.println("\n成功获取申请列表 - 数量: " + applications.size());
            
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            System.out.println("\n获取申请列表时出错:");
            System.out.println("异常类型: " + e.getClass().getName());
            System.out.println("异常消息: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取申请列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 获取职位的所有申请
    @GetMapping("/position/{positionId}")
    public List<Map<String, Object>> getPositionApplications(@PathVariable("positionId") Long positionId, HttpServletRequest request) {
        try {
            // 验证token
            String authHeader = request.getHeader("Authorization");
            String userIdStr = request.getHeader("userId");

            if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
                return Collections.emptyList();
            }

            return applicationService.getApplicationsByPositionId(positionId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // 更新申请状态
    @PutMapping("/{id}/status")
    public Map<String, Object> updateApplicationStatus(
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> statusData,
            HttpServletRequest request) {
        try {
            // 验证token
            String authHeader = request.getHeader("Authorization");
            String userIdStr = request.getHeader("userId");

            if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "未授权访问");
                return errorResult;
            }

            String status = statusData.get("status");
            if (status == null || status.isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "状态不能为空");
                return errorResult;
            }

            return applicationService.updateApplicationStatus(id, status);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "更新状态失败: " + e.getMessage());
            return errorResult;
        }
    }

    // 删除申请
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteApplication(@PathVariable("id") Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 验证token
            String authHeader = request.getHeader("Authorization");
            String userIdStr = request.getHeader("userId");

            if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
                result.put("success", false);
                result.put("message", "未授权访问");
                return result;
            }

            boolean deleteResult = applicationService.deleteApplication(id);
            result.put("success", deleteResult);
            result.put("message", deleteResult ? "删除成功" : "删除失败");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            return result;
        }
    }
} 