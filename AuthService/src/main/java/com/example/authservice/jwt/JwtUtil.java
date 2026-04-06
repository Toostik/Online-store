package com.example.authservice.jwt;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;


@Component
public class JwtUtil {
    private final PrivateKey privateKey;

    public JwtUtil(@Value("${app.private.key.path}") Resource resource) {
        try (InputStream is = resource.getInputStream()) {

            String key = new String(is.readAllBytes());
            key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            this.privateKey = KeyFactory.getInstance("EC").generatePrivate(spec);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки PUBLIC KEY", e);
        }
    }

    public String createToken(Map<String, Object> claims){

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuer("auth-service")
                    .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                    .signWith(privateKey)
                    .compact();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid claims for JWT creation: " + e.getMessage(), e);

        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid private key for ES256 algorithm", e);

        } catch (JwtException e) {
            throw new RuntimeException("Failed to create JWT token: " + e.getMessage(), e);

        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while creating JWT token", e);
        }


    }

}
