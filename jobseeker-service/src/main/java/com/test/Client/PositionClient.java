package com.test.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;

@FeignClient(name = "company-service")
public interface PositionClient {
    
    /**
     * 获取所有已发布的职位
     */
    @GetMapping("/api/positions/public")
    List<Map<String, Object>> getAllPublishedPositions(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("userId") String userId);
    
    /**
     * 根据关键词搜索职位
     */
    @GetMapping("/api/positions/search")
    List<Map<String, Object>> searchPositions(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("userId") String userId,
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "category", required = false) String category);

} 