package com.mycloud.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;

@Configuration
@ConditionalOnProperty(name = "gateway.security.jwt.mode", havingValue = "issuer")
public class IssuerJwtDecoderConfiguration {
    @Bean
    ReactiveJwtDecoder issuerJwtDecoder(
        @Value("${gateway.security.jwt.issuer-uri}") String issuerUri,
        @Value("${gateway.security.jwt.jwk-set-uri:}") String jwkSetUri
    ) {
        if (issuerUri == null || issuerUri.isBlank()) {
            throw new IllegalStateException("JWT issuer URI is required in issuer mode");
        }
        if (jwkSetUri == null || jwkSetUri.isBlank()) {
            return ReactiveJwtDecoders.fromIssuerLocation(issuerUri);
        }
        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerUri));
        return decoder;
    }
}
