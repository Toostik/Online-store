package com.example.authservice.jwt;

import com.example.authservice.exceptions.file.KeyLoadException;
import com.example.authservice.exceptions.token.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final PrivateKey privateKey;
    private final ECPublicKey publicKey;

    private final long accessExpiration;
    private final long refreshExpiration;

    private static final String ISSUER = "auth-service";

    public JwtUtil(
            @Value("${app.private.key.path}") Resource privateResource,
            @Value("${app.public.key.path}") Resource publicResource,
            @Value("${app.jwt.access-expiration}") long accessExpiration,// 4 часа для разработки
            @Value("${app.jwt.refresh-expiration}") long refreshExpiration // 7 дней
    ) {
        try {
            this.privateKey = loadPrivateKey(privateResource);
            this.publicKey = loadPublicKey(publicResource);
        } catch (Exception e) {
            throw new KeyLoadException("Key didn't load");
        }

        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String createAccessToken(String userId, List<String> roles) {

        if(userId == null){
            throw new TokenException("User id is null");
        }

        if(roles == null || roles.isEmpty()){
            throw new TokenException("Roles is empty");
        }

        return buildToken(userId, roles, "access", accessExpiration);
    }

    public String createRefreshToken(String userId) {

        if(userId == null){
            throw new TokenException("User id is null");
        }

        return buildToken(userId, null, "refresh", refreshExpiration);
    }

    private String buildToken(String userId, List<String> roles, String type, long expiration) {

        JwtBuilder builder = Jwts.builder()
                .setSubject(userId)
                .setIssuer(ISSUER)
                .claim("type", type)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey, SignatureAlgorithm.ES256);

        if (roles != null) {
            builder.claim("roles", roles);
        }

        return builder.compact();
    }

    public Claims parseToken(String token) {

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims;

        } catch (ExpiredJwtException e) {
            throw new TokenException("Token expired");

        } catch (SignatureException e) {
            throw new TokenException("Invalid signature");

        } catch (MalformedJwtException e) {
            throw new TokenException("Malformed token");

        } catch (Exception e) {
            throw new TokenException("Invalid token");
        }
    }

    public void validateAccessToken(String token) {
        Claims claims = parseToken(token);

        if (!"access".equals(claims.get("type"))) {
            throw new TokenException("Not an access token");
        }
    }

    public void validateRefreshToken(String token) {
        Claims claims = parseToken(token);

        if (!"refresh".equals(claims.get("type"))) {
            throw new TokenException("Not a refresh token");
        }

    }

    private PrivateKey loadPrivateKey(Resource resource) throws Exception {
        String key = readKey(resource);

        byte[] keyBytes = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance("EC")
                .generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private ECPublicKey loadPublicKey(Resource resource) throws Exception {
        String key = readKey(resource);

        byte[] keyBytes = Base64.getDecoder().decode(key);
        return (ECPublicKey) KeyFactory.getInstance("EC")
                .generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    private String readKey(Resource resource) throws Exception {
        try (InputStream is = resource.getInputStream()) {
            String key = new String(is.readAllBytes());

            return key.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
        }
    }
}