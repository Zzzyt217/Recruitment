package com.test.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "recruitment-service")
public interface ApplicationClient {
    
    @PostMapping("/api/applications")
    Map<String, Object> submitApplication(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("userId") String userId,
            @RequestBody Map<String, Object> application);
    
    @GetMapping("/api/applications/user/{userId}")
    List<Map<String, Object>> getUserApplications(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("userId") String adminId,
            @PathVariable("userId") Integer userId);
    
    @GetMapping("/api/applications/{id}")
    Map<String, Object> getApplicationById(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("userId") String adminId,
            @PathVariable("id") Long id);
    
    @PutMapping("/api/applications/{id}/status")
    Map<String, Object> updateApplicationStatus(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("userId") String adminId,
            @PathVariable("id") Long id,
            @RequestBody Map<String, String> statusData);
            
    @GetMapping("/api/applications/check")
    Map<String, Object> checkUserApplied(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("userId") String userIdHeader,
            @RequestParam("userId") Integer userId,
            @RequestParam("positionId") Long positionId);
            
    @DeleteMapping("/api/applications/{id}")
    Map<String, Object> deleteApplication(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("userId") String userId,
            @PathVariable("id") Long id);
} 