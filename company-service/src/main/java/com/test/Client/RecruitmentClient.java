package com.test.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@FeignClient(name = "recruitment-service")
public interface RecruitmentClient {
    
    @GetMapping("/api/applications/company/{companyId}")
    ResponseEntity<List<Map<String, Object>>> getApplicationsByCompanyId(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("userId") String adminId,
            @PathVariable("companyId") Long companyId);
    
    @GetMapping("/api/applications/position/{positionId}")
    List<Map<String, Object>> getApplicationsByPositionId(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("userId") String adminId,
            @PathVariable("positionId") Long positionId);
    
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
} 