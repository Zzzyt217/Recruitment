package com.test.Controller;

import com.test.Mapper.ResumeMapper;
import com.test.Pojo.Resume;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin // 添加CORS支持
public class ResumeController {
    
    @Resource
    private ResumeMapper resumeMapper;

    @GetMapping("/resume")
    public String resume() {
        return "resume";
    }

    @PostMapping("/api/resume/save")
    @ResponseBody
    public Map<String, Object> saveResume(
            HttpServletRequest request,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "desiredPosition", required = false) String desiredPosition,
            @RequestParam(value = "education", required = false) String education,
            @RequestParam(value = "experience", required = false) String experience,
            @RequestParam(value = "skills", required = false) String skills) {

        Map<String, Object> result = new HashMap<>();
        
        // 打印所有请求头
        System.out.println("=========== 请求头信息 ===========");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + request.getHeader(headerName));
        }
        
        // 获取userId和email
        String userIdStr = request.getHeader("userId");
        String email = request.getHeader("X-User-Email");

        System.out.println("userId: " + userIdStr);
        System.out.println("email: " + email);
        
        // 打印所有请求参数
        System.out.println("=========== 表单参数 ===========");
        request.getParameterMap().forEach((key, value) -> {
            System.out.println(key + ": " + (value.length > 0 ? value[0] : ""));
        });
        
        // 检查用户ID
        if (userIdStr == null || email == null || email.isEmpty()) {
            result.put("success", false);
            result.put("message", "用户信息缺失，请重新登录");
            return result;
        }
        
        // 检查必填字段
        if (name == null || name.isEmpty()) {
            result.put("success", false);
            result.put("message", "姓名不能为空");
            return result;
        }

        try {
            Integer userId = Integer.parseInt(userIdStr);
            
            // 查找用户是否已经有简历
            Resume existingResume = resumeMapper.findByUserId(userId);

            if (existingResume != null) {
                // 更新现有简历
                existingResume.setName(name);
                existingResume.setPhone(phone != null ? phone : "");
                existingResume.setDesiredPosition(desiredPosition != null ? desiredPosition : "");
                existingResume.setEducation(education != null ? education : "");
                existingResume.setExperience(experience != null ? experience : "");
                existingResume.setSkills(skills != null ? skills : "");
                // email和userId不更新，因为它们是用户标识
                resumeMapper.updateResume(existingResume);
                result.put("success", true);
                result.put("message", "简历更新成功");
            } else {
                // 插入新简历
                Resume newResume = new Resume();
                newResume.setUserId(userId);
                newResume.setEmail(email);
                newResume.setName(name);
                newResume.setPhone(phone != null ? phone : "");
                newResume.setDesiredPosition(desiredPosition != null ? desiredPosition : "");
                newResume.setEducation(education != null ? education : "");
                newResume.setExperience(experience != null ? experience : "");
                newResume.setSkills(skills != null ? skills : "");
                resumeMapper.insertResume(newResume);
                result.put("success", true);
                result.put("message", "简历保存成功");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "用户ID格式错误");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "操作失败: " + e.getMessage());
        }
        return result;
    }
} 