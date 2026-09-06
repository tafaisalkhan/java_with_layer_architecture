package com.mycloud.apigateway.ratelimit;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gateway")
public record GatewayRateLimitProperties(Map<String, RateLimit> rateLimits) {
    public GatewayRateLimitProperties {
        rateLimits = rateLimits == null ? Map.of() : Map.copyOf(rateLimits);
    }

    public record RateLimit(long capacity, long refillTokensPerSecond) {
        public RateLimit {
            if (capacity < 1 || refillTokensPerSecond < 1) {
                throw new IllegalArgumentException("rate-limit capacity and refill rate must be positive");
            }
        }
    }
}
