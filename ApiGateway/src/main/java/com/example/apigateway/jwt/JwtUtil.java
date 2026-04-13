package com.example.apigateway.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

//@Component
public class JwtUtil {
    private final PublicKey publicKey;

    public JwtUtil(@Value("${app.public.key.path}") Resource resource) {
        try (InputStream is = resource.getInputStream()) {

            String key = new String(is.readAllBytes());
            key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);

            this.publicKey = KeyFactory.getInstance("EC")
                    .generatePublic(new X509EncodedKeySpec(keyBytes));

        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки PUBLIC KEY", e);
        }
    }

    public Jws<Claims> validateAndExtractClaims(String bearerToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(publicKey)
                    .requireIssuer("auth-service")
                    .build().parseClaimsJws(bearerToken);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired", e);
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid token signature", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Malformed token", e);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }

    }

    public String extractUserId(String token) {
        return validateAndExtractClaims(token)
                .getBody()
                .get("userId", String.class);
    }

    public String extractRole(String token) {
        return validateAndExtractClaims(token)
                .getBody()
                .get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            validateAndExtractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
