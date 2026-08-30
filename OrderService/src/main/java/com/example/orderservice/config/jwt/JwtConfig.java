package com.example.orderservice.config.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();

        converter.setAuthorityPrefix("ROLE_");
        converter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);

        return jwtConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${app.public.key.path}") String path) {

        try (InputStream is = Files.newInputStream(Paths.get(path))) {

            String key = new String(is.readAllBytes());
            key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            ECPublicKey publicKey = (ECPublicKey) KeyFactory
                    .getInstance("EC")
                    .generatePublic(spec);

            com.nimbusds.jose.jwk.ECKey ecKey = new com.nimbusds.jose.jwk.ECKey.Builder(
                    com.nimbusds.jose.jwk.Curve.P_256,
                    publicKey
            ).build();

            com.nimbusds.jose.proc.SecurityContext context = null;

            com.nimbusds.jose.proc.JWSKeySelector<com.nimbusds.jose.proc.SecurityContext> selector =
                    new com.nimbusds.jose.proc.JWSVerificationKeySelector<>(
                            com.nimbusds.jose.JWSAlgorithm.ES256,
                            new com.nimbusds.jose.jwk.source.ImmutableJWKSet<>(
                                    new com.nimbusds.jose.jwk.JWKSet(ecKey)
                            )
                    );

            com.nimbusds.jwt.proc.DefaultJWTProcessor<com.nimbusds.jose.proc.SecurityContext> processor =
                    new com.nimbusds.jwt.proc.DefaultJWTProcessor<>();

            processor.setJWSKeySelector(selector);

            return new NimbusJwtDecoder(processor);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки PUBLIC KEY", e);
        }
    }
}
