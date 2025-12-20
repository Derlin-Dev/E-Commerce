package com.E_Commerce.API_Gateway.security.rules;

import org.springframework.http.HttpMethod;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Component
public class SecurityRules {

    private final AntPathMatcher matcher = new AntPathMatcher();

    private static final List<AccessRule> RULES = List.of(

            //User-services
            new AccessRule(HttpMethod.POST, "/e-commerce/api/v1/auth/**", null),
            new AccessRule(HttpMethod.GET, "/e-commerce/api/v1/user/**", List.of("ADMIN", "USER")),

            //Product-services /product
            new AccessRule(HttpMethod.GET, "/e-commerce/api/v1/product/**", null),
            new AccessRule(HttpMethod.POST, "/e-commerce/api/v1/product/**", List.of("ADMIN")),
            new AccessRule(HttpMethod.PUT, "/e-commerce/api/v1/product/**", List.of("ADMIN")),
            new AccessRule(HttpMethod.DELETE, "/e-commerce/api/v1/product/**", List.of("ADMIN")),

            //Product-services /category
            new AccessRule(HttpMethod.GET, "/e-commerce/api/v1/category/**", null),
            new AccessRule(HttpMethod.POST, "/e-commerce/api/v1/category/**", List.of("ADMIN"))

    );

    public AccessDecision evaluate(String path, HttpMethod httpMethod, List<String> roles){

        for (AccessRule rule: RULES) {
            if (rule.method() == httpMethod && matcher.match(rule.path(), path)) {

                if (rule.roles() == null) {
                    return AccessDecision.PERMIT;
                }

                if (roles == null){
                    return AccessDecision.AUTH_REQUIRED;
                }

                boolean allowed = roles.stream()
                        .anyMatch(rule.roles()::contains);
                return allowed ? AccessDecision.PERMIT : AccessDecision.FORBIDDEN;
            }
        }
        return AccessDecision.FORBIDDEN;
    }

}
