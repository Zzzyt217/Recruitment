package com.test.gateway.Filter;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.RedirectToGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Component
public class AuthFilter implements GlobalFilter , Ordered {

    @Value("${MyJwt.SECRET_KEY}")
    private String SECRET_KEY;

    //编写部分
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        //放行规则
        if (isWhitelisted(path)) {
            // auth
            return chain.filter(exchange);
        }

        // 放行静态资源  --  静态资源如果有就放在这一份
        if (path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/favicon.ico")) {
            return chain.filter(exchange);
        }

        // 2. 获取token
        String token = getTokenFromRequest(exchange);

//        // 存数据
//        exchange.getAttributes().put("USER_ID", "12345");
//
//        // 取数据
//        String userId = (String) exchange.getAttribute("USER_ID");

        if (token == null) {
//            return redirectToErrorPage(exchange, "/error/401", HttpStatus.UNAUTHORIZED);
            return redirectToErrorPage(exchange, "/auth/index", HttpStatus.NOT_FOUND);
        }

        // 3.令牌验证
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
            //e.printStackTrace(); // 打印异常，便于调试
//            if (e instanceof CallNotPermittedException) {
//                return redirectToErrorPage(exchange, "/fallback/service", HttpStatus.SERVICE_UNAVAILABLE);
//            }
            return redirectToErrorPage(exchange, "/error/401", HttpStatus.UNAUTHORIZED);
        }

    }

    // 辅助方法：从请求获取token
    private String getTokenFromRequest(ServerWebExchange exchange) {
        // 从Authorization头获取
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 从url获取 ??
        return exchange.getRequest().getQueryParams().getFirst("token");
    }

    // 重定向方法
    private Mono<Void> redirectToErrorPage(ServerWebExchange exchange,
                                           String errorPath,
                                           HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND); // 302重定向
        response.getHeaders().setLocation(URI.create(errorPath));
        return response.setComplete();
    }

    //执行优先级
    @Override
    public int getOrder() {
        return -100;
    }

//     辅助方法：检查路径是否在白名单
    private boolean isWhitelisted(String path) {
        return path.startsWith("/auth/") ||
                path.startsWith("/resume/") ||
//                path.startsWith("/jobseeker/") ||  不放行
//                path.startsWith("/error/") ||
                path.startsWith("/other/")||
                path.startsWith("/company/")||
                path.startsWith("/fallback/");
    }


}
