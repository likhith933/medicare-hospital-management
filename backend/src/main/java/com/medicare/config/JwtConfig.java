package com.medicare.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class JwtConfig {
    @Value("${medicare.jwt.secret}")
    private String secret;

    @Value("${medicare.jwt.expiration-ms}")
    private Long expirationMs;

    @Value("${medicare.jwt.refresh-expiration-ms}")
    private Long refreshExpirationMs;
}
