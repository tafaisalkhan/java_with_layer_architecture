package com.mycloud.apigateway.config;

import com.mycloud.apigateway.ratelimit.GatewayRateLimitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GatewayRateLimitProperties.class)
public class GatewayConfiguration {
}
