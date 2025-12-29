package com.E_Commerce.API_Gateway.security.filter;

import com.E_Commerce.API_Gateway.security.rules.AccessDecision;
import com.E_Commerce.API_Gateway.security.rules.SecurityRules;
import com.E_Commerce.API_Gateway.security.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter , Ordered {

    private final JwtUtil jwtUtil;
    private final SecurityRules securityRules;

    public JwtAuthFilter(JwtUtil jwtUtil, SecurityRules securityRules) {
        this.jwtUtil = jwtUtil;
        this.securityRules = securityRules;
    }

//    private static final List<String> PUBLIC_ROUTES = List.of(
//            "/e-commerce/api/v1/auth/login",
//            "/e-commerce/api/v1/auth/signup",
//            "/e-commerce/api/v1/product/get",
//            "/e-commerce/api/v1/category/get",
//            "/e-commerce/api/v1/category/getproductbycategory"
//    );
//
//    private final AntPathMatcher pathMatcher = new AntPathMatcher();
//
//    private boolean isPublicRoute(String path) {
//        return PUBLIC_ROUTES.stream()
//                .anyMatch(pattern -> pathMatcher.match(pattern, path));
//    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        AccessDecision decision = securityRules.evaluate(path, method, null);

        if (decision == AccessDecision.PERMIT){
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Token requerido");
        }

        String token = authHeader.substring(7);

        try {
            jwtUtil.validateToken(token);

            String userCode = jwtUtil.extractUserCode(token);
            List<String> roles = jwtUtil.extractRoles(token);

            decision = securityRules.evaluate(path, method, roles);

            if (decision == AccessDecision.FORBIDDEN){
                return unauthorized(exchange, "Acceso denegado");
            }

            ServerHttpRequest mutateRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Code", userCode)
                    .header("X-User-Roles", String.join(",", roles))
                    .build();

            return chain.filter(exchange.mutate().request(mutateRequest).build());
        } catch (JwtException e) {
            return unauthorized(exchange, e.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\": \"" + message + "\"}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
