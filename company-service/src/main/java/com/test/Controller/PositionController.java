package com.test.Controller;

import com.test.Mapper.PositionMapper;
import com.test.Mapper.CompanyMapper;
import com.test.Pojo.Position;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;
import javax.annotation.Resource;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.util.StringUtils;
import java.util.Arrays;

@Controller
@CrossOrigin
public class PositionController {
    
    @Resource
    private PositionMapper positionMapper;
    
    @Resource
    private CompanyMapper companyMapper;
    
    // 职位列表页面
    @GetMapping("/positions")
    public String positionList(HttpServletRequest request, Model model) {
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            return "redirect:/auth/login";
        }
        
        return "position-list";
    }

    // 职位发布页面
    @GetMapping("/positions/new")
    public String newPosition(HttpServletRequest request,
                             @RequestParam(required = false) String title,
                             @RequestParam(required = false) String category,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) String salaryRange,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) String requirements,
                             Model model) {
        // 获取Authorization header
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");
        
        // 如果请求头中没有认证信息，尝试从cookie中获取
        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            // 如果有URL参数，说明是从其他页面跳转过来的，尝试直接保存
            if (title != null && !title.isEmpty() && category != null && !category.isEmpty()) {
                try {
                    // 创建Position对象
                    Position position = new Position();
                    position.setTitle(title);
                    position.setCategory(category);
                    position.setLocation(location != null ? location : "");
                    position.setSalaryRange(salaryRange != null ? salaryRange : "");
                    position.setDescription(description != null ? description : "");
                    position.setRequirements(requirements != null ? requirements : "");
                    
                    // 从cookie中获取userId
                    javax.servlet.http.Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (javax.servlet.http.Cookie cookie : cookies) {
                            if ("userId".equals(cookie.getName())) {
                                userIdStr = cookie.getValue();
                                break;
                            }
                        }
                    }
                    
                    if (userIdStr != null && !userIdStr.isEmpty()) {
                        position.setCompanyId(Long.parseLong(userIdStr));
                        position.setCreatedAt(new Date());
                        position.setUpdatedAt(new Date());
                        position.setStatus("PUBLISHED");
                        
                        int result = positionMapper.insert(position);
                        if (result > 0) {
                            return "redirect:/auth/index?success=true";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            return "redirect:/auth/login";
        }
        
        // 将URL参数添加到模型中，以便在表单中显示
        if (title != null) model.addAttribute("title", title);
        if (category != null) model.addAttribute("category", category);
        if (location != null) model.addAttribute("location", location);
        if (salaryRange != null) model.addAttribute("salaryRange", salaryRange);
        if (description != null) model.addAttribute("description", description);
        if (requirements != null) model.addAttribute("requirements", requirements);
        
        return "position-form";
    }

    // 获取职位列表API
    @GetMapping("/api/positions")
    @ResponseBody
    public List<Map<String, Object>> getPositions(HttpServletRequest request) {
        String userIdStr = request.getHeader("userId");
        
        if (userIdStr == null || userIdStr.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            // 根据userId查询对应的公司信息
            Integer userId = Integer.parseInt(userIdStr);
            com.test.Pojo.Company company = companyMapper.findByUserId(userId);
            
            if (company == null) {
                return new ArrayList<>();
            }
            
            // 使用公司ID查询职位列表
            Long companyId = company.getId();
            List<Position> positions = positionMapper.findByCompanyId(companyId);
            List<Map<String, Object>> result = new ArrayList<>();

            for (Position position : positions) {
                Map<String, Object> positionMap = new HashMap<>();
                positionMap.put("id", position.getId());
                positionMap.put("title", position.getTitle());
                positionMap.put("category", position.getCategory());
                positionMap.put("location", position.getLocation());
                positionMap.put("salary", position.getSalaryRange());
                positionMap.put("status", position.getStatus());
                positionMap.put("description", position.getDescription());
                positionMap.put("requirements", position.getRequirements());
                positionMap.put("updateTime", position.getUpdatedAt());
                result.add(positionMap);
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 获取单个职位API
    @GetMapping("/api/positions/{id}")
    @ResponseBody
    public Position getPosition(@PathVariable("id") Long id, HttpServletRequest request) {
        String userIdStr = request.getHeader("userId");
        if (userIdStr == null || userIdStr.isEmpty()) {
            return null;
        }

        Long companyId = Long.parseLong(userIdStr);
        return positionMapper.findByIdAndCompanyId(id, companyId);
    }

    // 创建职位API
    @PostMapping("/api/positions")
    @ResponseBody
    public Map<String, Object> createPosition(
            HttpServletRequest request,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "salaryRange", required = false) String salaryRange,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "requirements", required = false) String requirements,
            @RequestParam(value = "userId", required = false) String formUserId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 获取userId，优先从请求头获取，如果没有则从表单参数获取
            String userIdStr = request.getHeader("userId");
            if (userIdStr == null || userIdStr.isEmpty()) {
                userIdStr = formUserId;
            }
            
            if (userIdStr == null || userIdStr.isEmpty()) {
                response.put("success", false);
                response.put("message", "未找到用户信息");
                return response;
            }
            
            // 验证必填字段
            if (title == null || title.isEmpty()) {
                response.put("success", false);
                response.put("message", "职位名称不能为空");
                return response;
            }
            
            if (category == null || category.isEmpty()) {
                response.put("success", false);
                response.put("message", "职位类别不能为空");
                return response;
            }
            
            if (location == null || location.isEmpty()) {
                response.put("success", false);
                response.put("message", "工作地点不能为空");
                return response;
            }
            
            if (salaryRange == null || salaryRange.isEmpty()) {
                response.put("success", false);
                response.put("message", "薪资范围不能为空");
                return response;
            }

            // 根据userId查询对应的公司信息
            Integer userId = Integer.parseInt(userIdStr);
            com.test.Pojo.Company company = companyMapper.findByUserId(userId);
            
            if (company == null) {
                response.put("success", false);
                response.put("message", "请先完善企业信息");
                return response;
            }
            
            // 创建Position对象
            Position position = new Position();
            position.setTitle(title);
            position.setCategory(category);
            position.setLocation(location);
            position.setSalaryRange(salaryRange);
            position.setDescription(description != null ? description : "");
            position.setRequirements(requirements != null ? requirements : "");
            position.setCompanyId(company.getId()); // 使用公司ID，而不是用户ID
            position.setCreatedAt(new Date());
            position.setUpdatedAt(new Date());
            position.setStatus("PUBLISHED");
            
            System.out.println("准备保存职位: " + position.getTitle() + ", 企业ID: " + position.getCompanyId());
            int result = positionMapper.insert(position);
            System.out.println("保存职位结果: " + result);
            
            response.put("success", result > 0);
            response.put("message", result > 0 ? "发布成功" : "发布失败");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "发布失败：" + e.getMessage());
        }
        
        return response;
    }

    // 验证职位信息
        private String validatePosition(Position position) {
            // 1. 必填字段验证
            if (!StringUtils.hasText(position.getTitle())) {
                return "职位名称不能为空";
            }
            if (!StringUtils.hasText(position.getCategory())) {
                return "职位类别不能为空";
            }
            if (!StringUtils.hasText(position.getLocation())) {
                return "工作地点不能为空";
            }
            if (!StringUtils.hasText(position.getSalaryRange())) {
                return "薪资范围不能为空";
            }
            if (!StringUtils.hasText(position.getDescription())) {
                return "职位描述不能为空";
            }
            if (!StringUtils.hasText(position.getRequirements())) {
                return "任职要求不能为空";
            }

            // 2. 字段长度验证
            if (position.getTitle().length() > 100) {
                return "职位名称不能超过100个字符";
            }
            if (position.getCategory().length() > 50) {
                return "职位类别不能超过50个字符";
            }
            if (position.getLocation().length() > 50) {
                return "工作地点不能超过50个字符";
            }
            if (position.getSalaryRange().length() > 50) {
                return "薪资范围不能超过50个字符";
            }

            // 3. 薪资范围格式验证（可选）
            String salaryRange = position.getSalaryRange().toLowerCase();
            if (!salaryRange.matches("^\\d+k-\\d+k$") && !salaryRange.matches("^面议$")) {
                return "薪资范围格式不正确（例如：15k-25k 或 面议）";
            }

            return null; // 验证通过
    }

    // 更新职位API
    @PutMapping("/api/positions/{id}")
    @ResponseBody
    public Map<String, Object> updatePosition(@PathVariable("id") Long id,
                                            @RequestBody Position position,
                                            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String userIdStr = request.getHeader("userId");
            if (userIdStr == null || userIdStr.isEmpty()) {
                response.put("success", false);
                response.put("message", "未找到用户信息");
                return response;
            }

            // 根据userId查询对应的公司信息
            Integer userId = Integer.parseInt(userIdStr);
            com.test.Pojo.Company company = companyMapper.findByUserId(userId);
            
            if (company == null) {
                response.put("success", false);
                response.put("message", "请先完善企业信息");
                return response;
            }
            
            // 检查该职位是否属于当前公司
            Position existingPosition = positionMapper.findByIdAndCompanyId(id, company.getId());
            if (existingPosition == null) {
                response.put("success", false);
                response.put("message", "没有权限修改此职位");
                return response;
            }

            // 设置职位信息
            position.setId(id);
            position.setCompanyId(company.getId());
            position.setUpdatedAt(new Date());
            // 保留原有的创建时间和状态
            position.setCreatedAt(existingPosition.getCreatedAt());
            position.setStatus(existingPosition.getStatus());

            int result = positionMapper.update(position);

            response.put("success", result > 0);
            response.put("message", result > 0 ? "更新成功" : "更新失败");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
        }
        
        return response;
    }

    // 职位编辑页面
    @GetMapping("/positions/edit/{id}")
    public String editPosition(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        String authHeader = request.getHeader("Authorization");
        String userIdStr = request.getHeader("userId");

        if (authHeader == null || !authHeader.startsWith("Bearer ") || userIdStr == null || userIdStr.isEmpty()) {
            return "redirect:/auth/login";
        }

        try {
            // 根据userId查询对应的公司信息
            Integer userId = Integer.parseInt(userIdStr);
            com.test.Pojo.Company company = companyMapper.findByUserId(userId);
            
            if (company == null) {
                return "redirect:/auth/index?error=company_not_found";
            }
            
            // 使用公司ID和职位ID查询职位详情
            Position position = positionMapper.findByIdAndCompanyId(id, company.getId());
            
            if (position == null) {
                return "redirect:/company/positions?error=position_not_found";
            }
            
            // 将职位信息添加到模型中
            model.addAttribute("position", position);
            model.addAttribute("title", position.getTitle());
            model.addAttribute("category", position.getCategory());
            model.addAttribute("location", position.getLocation());
            model.addAttribute("salaryRange", position.getSalaryRange());
            model.addAttribute("description", position.getDescription());
            model.addAttribute("requirements", position.getRequirements());
            model.addAttribute("positionId", position.getId());
            model.addAttribute("isEdit", true);
            
            return "position-form";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/company/positions?error=load_failed";
        }
    }

    // 删除职位API
    @DeleteMapping("/api/positions/{id}")
    @ResponseBody
    public Map<String, Object> deletePosition(@PathVariable("id") Long id, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userIdStr = request.getHeader("userId");
            if (userIdStr == null || userIdStr.isEmpty()) {
                response.put("success", false);
                response.put("message", "未找到用户信息");
                return response;
            }
            
            // 根据userId查询对应的公司信息
            Integer userId = Integer.parseInt(userIdStr);
            com.test.Pojo.Company company = companyMapper.findByUserId(userId);
            
            if (company == null) {
                response.put("success", false);
                response.put("message", "请先完善企业信息");
                return response;
            }
            
            // 检查该职位是否属于当前公司
            Position existingPosition = positionMapper.findByIdAndCompanyId(id, company.getId());
            if (existingPosition == null) {
                response.put("success", false);
                response.put("message", "没有权限删除此职位");
                return response;
            }
            
            // 删除职位
            int result = positionMapper.deleteById(id, company.getId());
            
            response.put("success", result > 0);
            response.put("message", result > 0 ? "删除成功" : "删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "删除失败：" + e.getMessage());
        }
        
        return response;
    }

    // 获取所有公开职位API（供求职者使用）
    @GetMapping("/api/positions/public")
    @ResponseBody
    public List<Map<String, Object>> getAllPublishedPositions(HttpServletRequest request) {
        try {
            // 获取所有状态为PUBLISHED的职位
            List<Position> positions = positionMapper.findAllPublished();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Position position : positions) {
                Map<String, Object> positionMap = new HashMap<>();
                positionMap.put("id", position.getId());
                positionMap.put("title", position.getTitle());
                positionMap.put("company", getCompanyName(position.getCompanyId()));
                positionMap.put("location", position.getLocation());
                positionMap.put("salary", position.getSalaryRange());
                positionMap.put("description", position.getDescription());
                positionMap.put("requirements", position.getRequirements());
                positionMap.put("category", position.getCategory());
                positionMap.put("updateTime", position.getUpdatedAt());
                
                result.add(positionMap);
            }
            
            System.out.println("获取所有公开职位，数量: " + result.size());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 根据关键词搜索职位API（供求职者使用）
    @GetMapping("/api/positions/search")
    @ResponseBody
    public List<Map<String, Object>> searchPositions(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "category", required = false) String category,
            HttpServletRequest request) {
        try {
            // 获取所有状态为PUBLISHED的职位
            List<Position> positions = positionMapper.findAllPublished();
            List<Map<String, Object>> result = new ArrayList<>();

            // 转换为小写以便不区分大小写搜索
            String lowerKeyword = keyword.toLowerCase();
            String lowerLocation = location != null ? location.toLowerCase() : null;
            String lowerCategory = category != null ? category.toLowerCase() : null;

            for (Position position : positions) {
                // 获取公司名称
                String companyName = getCompanyName(position.getCompanyId()).toLowerCase();
                
                // 应用筛选条件
                boolean matchKeyword = position.getTitle().toLowerCase().contains(lowerKeyword) ||
                                      position.getDescription().toLowerCase().contains(lowerKeyword) ||
                                      position.getRequirements().toLowerCase().contains(lowerKeyword) ||
                                      companyName.contains(lowerKeyword);
                
                boolean matchLocation = lowerLocation == null || 
                                       position.getLocation().toLowerCase().contains(lowerLocation);
                
                boolean matchCategory = lowerCategory == null || 
                                       position.getCategory().toLowerCase().contains(lowerCategory);
                
                if (matchKeyword && matchLocation && matchCategory) {
                    Map<String, Object> positionMap = new HashMap<>();
                    positionMap.put("id", position.getId());
                    positionMap.put("title", position.getTitle());
                    positionMap.put("company", getCompanyName(position.getCompanyId()));
                    positionMap.put("location", position.getLocation());
                    positionMap.put("salary", position.getSalaryRange());
                    positionMap.put("description", position.getDescription());
                    positionMap.put("requirements", position.getRequirements());
                    positionMap.put("category", position.getCategory());
                    positionMap.put("updateTime", position.getUpdatedAt());

                    
                    result.add(positionMap);
                }
            }
            
            System.out.println("搜索职位，关键词: " + keyword + ", 结果数量: " + result.size());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // 辅助方法：获取公司名称
    private String getCompanyName(Long companyId) {
        try {
            com.test.Pojo.Company company = companyMapper.findById(companyId);
            return company != null ? company.getName() : "未知公司";
        } catch (Exception e) {
            return "未知公司";
        }
    }

} 