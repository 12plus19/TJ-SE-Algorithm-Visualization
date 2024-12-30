package com.blog.wang.algorithmgateway.filter;

import com.blog.wang.algorithmgateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter {

    // 硬编码不需要 JWT 验证的路径
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/user/login",
            "/user/register",
            "/user/sendCode"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        // 检查请求路径是否在排除列表中
        if (EXCLUDE_PATHS.contains(path)) {
            // 如果路径在排除列表中，直接继续处理请求
            return chain.filter(exchange);
        }
        // 从请求头中获取 JWT 令牌
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.err.println("JWT 验证失败: " + authHeader);
        if (authHeader == null) {
            // 如果没有 JWT，返回 401 Unauthorized
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        try {
            // 验证 JWT 令牌
            Claims claims = JwtUtils.parseJWT(authHeader);
            // 可以在这里将 claims 存储到请求属性中，以供后续使用
            exchange.getRequest().mutate().header("claims", claims.toString()).build();
        } catch (Exception e) {
            // 如果 JWT 验证失败，返回 401 Unauthorized
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 继续处理请求
        return chain.filter(exchange);
    }
}