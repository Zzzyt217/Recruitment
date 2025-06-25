package com.test.Controller;

import com.test.Mapper.CompanyMapper;
import com.test.Pojo.Company;
import com.test.Client.RecruitmentClient;
import com.test.Client.JobseekerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin // 添加CORS支持
public class CompanyController {

    @Resource
    private CompanyMapper companyMapper;

    @Resource
    private RecruitmentClient recruitmentClient;

    @Resource
    private JobseekerClient jobseekerClient;

    // 增加根路径访问，重定向到company-profile
    @GetMapping("/")
    public String root() {
        return "redirect:/company-profile";
    }

    // 申请管理页面
    @GetMapping("/applications")
    public String applications(HttpServletRequest request, Model model, 
                             @RequestParam(required = false) String status) {
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            return "redirect:/auth/index";
        }

        try {
            // 获取企业信息
            Integer userId = Integer.parseInt(userIdStr);
            Company company = companyMapper.findByUserId(userId);
            
            if (company == null) {
                return "redirect:/company-profile";
            }
            
            // 获取该企业的所有申请
            ResponseEntity<List<Map<String, Object>>> response = null;
            try {
                response = recruitmentClient.getApplicationsByCompanyId(
                    authHeader,
                    userIdStr,
                    company.getId()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if (response != null && response.getBody() != null) {
                List<Map<String, Object>> applications = response.getBody();
                
                // 如果指定了状态，进行筛选
                if (status != null && !status.isEmpty()) {
                    applications = applications.stream()
                        .filter(app -> {
                            String appStatus = String.valueOf(app.get("status")).toUpperCase();
                            return appStatus.equals(status.toUpperCase());
                        })
                        .collect(java.util.stream.Collectors.toList());
                }
                
                model.addAttribute("applicationList", applications);
                // 将当前选中的状态添加到模型中
                model.addAttribute("selectedStatus", status);
            } else {
                model.addAttribute("applicationList", java.util.Collections.emptyList());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/auth/index";
        }
        
        return "applications";
    }

    // 更新申请状态
    @PutMapping("/applications/{id}/status")
    @ResponseBody
    public Map<String, Object> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusData,
            HttpServletRequest request) {
        
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        // 打印调试信息
        System.out.println("更新申请状态请求 - 申请ID: " + id);
        System.out.println("状态数据: " + statusData);
        System.out.println("Authorization头: " + (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null"));
        System.out.println("用户ID: " + userIdStr);
        
        // 验证请求参数
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "认证信息缺失或无效");
            return errorResult;
        }
        
        if (userIdStr == null || userIdStr.isEmpty()) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "用户ID缺失");
            return errorResult;
        }
        
        if (statusData == null || !statusData.containsKey("status") || statusData.get("status") == null) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "状态数据缺失");
            return errorResult;
        }
        
        try {
            Map<String, Object> result = recruitmentClient.updateApplicationStatus(
                authHeader,
                userIdStr,
                id,
                statusData
            );
            
            System.out.println("更新申请状态响应: " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "更新状态失败: " + e.getMessage());
            return errorResult;
        }
    }

    // 主要路径 - 企业中心页面
    @GetMapping("/company-profile")
    public String companyProfile(HttpServletRequest request, Model model) {
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            return "redirect:/auth/login";
        }

        try {
            // 获取企业信息
            Integer userId = Integer.parseInt(userIdStr);
            Company company = companyMapper.findByUserId(userId);
            
            // 如果没有找到企业信息，创建一个空的Company对象
            if (company == null) {
                company = new Company();
                company.setUserId(userId);
                company.setVerified(false);
            }
            
            // 将company对象添加到model中
            model.addAttribute("company", company);
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/auth/login";
        }
        
        return "company-profile";
    }

    // 兼容路径 - 重定向到主路径
    @GetMapping("/profile")
    public String profile(HttpServletRequest request, Model model) {
        return companyProfile(request, model);
    }

    @PostMapping("/api/company/save")
    @ResponseBody
    public Map<String, Object> saveCompany(
            HttpServletRequest request,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "industry", required = false) String industry,
            @RequestParam(value = "scale", required = false) String scale,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "userId", required = false) String formUserId) {

        Map<String, Object> result = new HashMap<>();

        // 获取userId，优先从请求头获取，如果没有则从表单参数获取
        String userIdStr = request.getHeader("userId");
        if (userIdStr == null || userIdStr.isEmpty()) {
            userIdStr = formUserId;
        }

        // 检查用户ID
        if (userIdStr == null || userIdStr.isEmpty()) {
            result.put("success", false);
            result.put("message", "用户信息缺失，请重新登录");
            return result;
        }

        // 检查必填字段
        if (name == null || name.isEmpty()) {
            result.put("success", false);
            result.put("message", "公司名称不能为空");
            return result;
        }

        try {
            Integer userId = Integer.parseInt(userIdStr);

            // 查找用户是否已经有企业信息
            Company existingCompany = companyMapper.findByUserId(userId);

            if (existingCompany != null) {
                // 更新现有企业信息
                existingCompany.setName(name);
                existingCompany.setIndustry(industry != null ? industry : "");
                existingCompany.setScale(scale != null ? scale : "");
                existingCompany.setAddress(address != null ? address : "");
                existingCompany.setDescription(description != null ? description : "");
                companyMapper.updateCompany(existingCompany);
                result.put("success", true);
                result.put("message", "企业信息更新成功");
            } else {
                // 插入新企业信息
                Company company = new Company();
                company.setUserId(userId);
                company.setName(name);
                company.setIndustry(industry != null ? industry : "");
                company.setScale(scale != null ? scale : "");
                company.setAddress(address != null ? address : "");
                company.setDescription(description != null ? description : "");
                company.setVerified(false); // 新企业默认未认证
                companyMapper.insertCompany(company);
                result.put("success", true);
                result.put("message", "企业信息保存成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "操作失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/api/company/{userId}")
    @ResponseBody
    public ResponseEntity<Company> getCompanyByUserId(@PathVariable("userId") Integer userId) {
        try {
            Company company = companyMapper.findByUserId(userId);
            if (company != null) {
                return ResponseEntity.ok(company);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 查看求职者简历
    @GetMapping("/view-resume/{userId}")
    public String viewResume(@PathVariable("userId") Integer userId, Model model, HttpServletRequest request) {
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String companyUserIdStr = request.getHeader("userId");
        
        // 添加请求头调试日志
        System.out.println("Authorization header: " + (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null"));
        System.out.println("userId header: " + companyUserIdStr);
        
        // 验证token
        if (authHeader == null || !authHeader.startsWith("Bearer ") || companyUserIdStr == null || companyUserIdStr.isEmpty()) {
            System.out.println("验证失败，重定向到登录页面");
            return "redirect:/auth/login";
        }

        try {
            System.out.println("开始调用jobseekerClient.getResumeByUserId, userId: " + userId);
            // 使用JobseekerClient直接获取求职者简历
            ResponseEntity<Object> responseEntity = jobseekerClient.getResumeByUserId(userId);
            
            // 添加调试日志
            System.out.println("获取到的简历信息: " + responseEntity);
            System.out.println("响应状态码: " + responseEntity.getStatusCode());
            
            // 处理响应
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                // 将简历信息直接添加到模型中
                model.addAttribute("resume", responseEntity.getBody());
                System.out.println("简历信息已添加到模型");
            } else {
                model.addAttribute("resume", null);
                model.addAttribute("errorMessage", "无法获取简历信息");
                System.out.println("响应成功但简历为空");
            }
            
        } catch (Exception e) {
            System.out.println("获取简历信息时出错: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("resume", null);
            model.addAttribute("errorMessage", "获取简历信息时出错: " + e.getMessage());
        }
        
        System.out.println("返回view-resume视图");
        return "view-resume";
    }
} 