package com.E_Commerce.API_Gateway.security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ========================
    // Parseo centralizado
    // ========================
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ========================
    // Validación
    // ========================
    public void validateToken(String token) {

        Claims claims = parseToken(token);

        if (claims.getSubject() == null || claims.getSubject().isBlank()) {
            throw new JwtException("Token sin subject");
        }

        if (claims.getExpiration() == null ||
                claims.getExpiration().before(new Date())) {
            throw new JwtException("Token expirado");
        }

        if (claims.get("roles") == null) {
            throw new JwtException("Token sin roles");
        }
    }

    // ========================
    // Extractores
    // ========================
    public String extractUserId(String token) {
        return parseToken(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = parseToken(token).get("roles");

        if (roles instanceof List<?> roleList) {
            return roleList.stream()
                    .map(Object::toString)
                    .toList();
        }

        throw new JwtException("Formato de roles inválido");
    }

    public Date extractExpiration(String token) {
        return parseToken(token).getExpiration();
    }
}

