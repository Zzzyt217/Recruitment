package com.test.gateway.Controller;

import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class FallbackController {
    
    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    @GetMapping("/fallback/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback(ServerWebExchange exchange) {
        Exception exception = exchange.getAttribute(ErrorAttributes.ERROR_ATTRIBUTE);
        log.error("Auth service fallback triggered. Error: ", exception);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("success", false);
        
        if (exception instanceof ConnectException) {
            result.put("message", "认证服务暂时不可用，系统正在自动重试连接...");
            result.put("retry_after", 2);
        } else {
            result.put("message", "认证服务暂时不可用，请稍后重试");
            result.put("retry_after", 5);
        }
        
        result.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity
            .status(503)
            .header("Retry-After", String.valueOf(result.get("retry_after")))
            .body(result));
    }

    @GetMapping("/fallback/jobseeker")
    public Mono<ResponseEntity<Map<String, Object>>> jobseekerFallback(ServerWebExchange exchange) {
        Exception exception = exchange.getAttribute(ErrorAttributes.ERROR_ATTRIBUTE);
        log.error("Jobseeker service fallback triggered. Error: ", exception);
        return createFallbackResponse("求职者服务暂时不可用，请稍后重试");
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 503);
        result.put("success", false);
        result.put("message", message);
        result.put("retry_after", 5);
        result.put("timestamp", System.currentTimeMillis());
        
        return Mono.just(ResponseEntity
            .status(503)
            .header("Retry-After", "5")
            .body(result));
    }
}
