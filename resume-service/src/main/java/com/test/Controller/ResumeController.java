package com.test.Controller;

import com.test.Pojo.Resume;
import com.test.Service.ResumeService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    
    @Resource
    private ResumeService resumeService;

    @PostMapping("/sync")
    public Map<String, Object> syncResume(@RequestBody Resume resume) {
        Map<String, Object> result = new HashMap<>();
        try {
            resumeService.syncResume(resume);
            result.put("success", true);
            result.put("message", "简历同步成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "简历同步失败: " + e.getMessage());
        }
        return result;
    }
} 