package com.example.apigateway.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


@Configuration
public class JwtConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );

        return converter;
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>>
    jwtAuthenticationConverter(
            JwtAuthenticationConverter jwtAuthConverter) {

        return new ReactiveJwtAuthenticationConverterAdapter(
                jwtAuthConverter
        );
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(
            @Value("${app.public.key.path}") Resource resource) {

        try (InputStream is = resource.getInputStream()) {

            String key = new String(is.readAllBytes());

            key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);

            X509EncodedKeySpec spec =
                    new X509EncodedKeySpec(keyBytes);

            ECPublicKey publicKey =
                    (ECPublicKey) KeyFactory
                            .getInstance("EC")
                            .generatePublic(spec);

            ECKey ecKey = new ECKey.Builder(
                    Curve.P_256,
                    publicKey
            ).build();

            JWSKeySelector<SecurityContext> selector =
                    new JWSVerificationKeySelector<>(
                            JWSAlgorithm.ES256,
                            new ImmutableJWKSet<>(
                                    new JWKSet(ecKey)
                            )
                    );

            DefaultJWTProcessor<SecurityContext> processor =
                    new DefaultJWTProcessor<>();

            processor.setJWSKeySelector(selector);

            JwtDecoder decoder =
                    new NimbusJwtDecoder(processor);

            return token ->
                    Mono.fromCallable(
                            () -> decoder.decode(token)
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Ошибка загрузки PUBLIC KEY",
                    e
            );
        }
    }

}
