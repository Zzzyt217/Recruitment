package com.test.Controller;

import com.test.Mapper.CompanyMapper;
import com.test.Pojo.Company;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin // 添加CORS支持
public class CompanyController {

    @Resource
    private CompanyMapper companyMapper;

    // 增加根路径访问，重定向到company-profile
    @GetMapping("/")
    public String root() {
        return "redirect:/company-profile";
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
} 