package com.test.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GlobalFilter, Ordered {
    private static final String SECRET_KEY = "yourSecretKey";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.equals("/auth/login") || path.equals("/auth/register") || path.equals("/auth/code") || 
            path.equals("/auth/api/user-info") || path.equals("/auth/index") || path.equals("/auth/api/logout") || path.equals("/auth/logout")) {
            // 登录、注册、验证码、用户信息API、首页、退出登录 直接放行
            return chain.filter(exchange);
        }
        
        String token = null;
        
        // 1. 首先尝试从Authorization头获取token
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        
        // 2. 如果没有Authorization头，尝试从表单参数获取token
        if (token == null) {
            String formToken = exchange.getRequest().getQueryParams().getFirst("token");
            if (formToken != null && !formToken.isEmpty()) {
                token = formToken;
            }
        }
        
        // 如果都没有token，返回401错误
        if (token == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            // 可将claims中的信息传递给下游服务
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("userId", claims.get("id").toString())
                    .header("username", claims.getSubject())
                    .header("role", claims.get("role").toString())
                    .header("X-User-Email", claims.get("eemail") != null ? claims.get("eemail").toString() : "")
                    .header("Authorization", "Bearer " + token)
                    .build();
            
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            e.printStackTrace(); // 打印异常，便于调试
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
} 