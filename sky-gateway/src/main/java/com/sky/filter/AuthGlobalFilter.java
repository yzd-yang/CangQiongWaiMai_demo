package com.sky.filter;

import com.sky.constant.JwtClaimsConstant;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtProperties jwtProperties;

    // 白名单：登录、公开接口、文档等
    private static final List<String> WHITE_LIST = List.of(
        "/admin/employee/login",
        "/user/user/login",
        "/user/shop/status",
        "/ws/**"
        // 按需加 /doc.html 等
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1) 白名单直接放行
        if (isWhite(path)) {
            return chain.filter(exchange);
        }

        // 2) 按路径选密钥 + Token 头名 + claim 键
        boolean isAdmin = path.startsWith("/admin/");
        String tokenName = isAdmin
            ? jwtProperties.getAdminTokenName()   // "token"
            : jwtProperties.getUserTokenName();  // "authentication"
        String secret = isAdmin
            ? jwtProperties.getAdminSecretKey()
            : jwtProperties.getUserSecretKey();
        String claimKey = isAdmin
            ? JwtClaimsConstant.EMP_ID           // "empId"
            : JwtClaimsConstant.USER_ID;         // "userId"

        String token = exchange.getRequest().getHeaders().getFirst(tokenName);
        if (token == null || token.isBlank()) {
            return unauthorized(exchange);
        }

        try {
            Claims claims = JwtUtil.parseJWT(secret, token);
            String userId = claims.get(claimKey).toString();

            // 3) 写入下游请求头（不要改客户端原始 token 头，额外加）
            ServerHttpRequest request = exchange.getRequest().mutate()
                .header("user-info", userId)
                .header("user-type", isAdmin ? "admin" : "user")  // 可选
                .build();

            return chain.filter(exchange.mutate().request(request).build());
        } catch (Exception e) {
            return unauthorized(exchange);
        }
    }
    private boolean isWhite(String path) {
        if (path.startsWith("/ws/")) return true;
        return WHITE_LIST.stream().anyMatch(path::equals);
    }

    @Override
    public int getOrder() {
        return -100; // 尽量靠前
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // 可选：写 JSON body，风格对齐 Result
        return exchange.getResponse().setComplete();
    }
}