package com.test.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "resume-service")//feign客户端定义
public interface ResumeClient {
    
    @PostMapping("/api/resumes/sync")
    Map<String, Object> syncResume(@RequestBody Map<String, Object> resume);
} 