package com.E_Commerce.API_Gateway.security.rules;

import org.springframework.http.HttpMethod;

import java.util.List;

public record AccessRule(

        HttpMethod method,
        String path,
        List<String> roles

) { }
