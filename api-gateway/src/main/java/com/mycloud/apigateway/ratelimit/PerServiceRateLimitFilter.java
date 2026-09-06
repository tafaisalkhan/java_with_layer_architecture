package com.mycloud.apigateway.ratelimit;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class PerServiceRateLimitFilter implements GlobalFilter, Ordered {
    private final GatewayRateLimitProperties properties;
    private final Clock clock;
    private final Map<ClientService, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Autowired
    public PerServiceRateLimitFilter(GatewayRateLimitProperties properties) {
        this(properties, Clock.systemUTC());
    }

    PerServiceRateLimitFilter(GatewayRateLimitProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String service = serviceName(exchange.getRequest().getPath().value());
        GatewayRateLimitProperties.RateLimit policy = properties.rateLimits().get(service);
        if (policy == null) {
            return chain.filter(exchange);
        }
        String client = clientIdentity(exchange);
        TokenBucket bucket = buckets.computeIfAbsent(new ClientService(client, service), ignored ->
            new TokenBucket(policy.capacity(), clock.millis()));
        if (!bucket.tryConsume(policy, clock.millis())) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().set("Retry-After", "1");
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private String serviceName(String path) {
        String[] segments = path.split("/");
        return segments.length > 2 ? segments[2] : "gateway";
    }

    private String clientIdentity(ServerWebExchange exchange) {
        String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return exchange.getRequest().getRemoteAddress() == null
            ? "unknown" : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }

    private record ClientService(String client, String service) {}

    private static final class TokenBucket {
        private double tokens;
        private long lastRefillMillis;

        private TokenBucket(long capacity, long now) {
            this.tokens = capacity;
            this.lastRefillMillis = now;
        }

        private synchronized boolean tryConsume(GatewayRateLimitProperties.RateLimit policy, long now) {
            long elapsed = Math.max(0, now - lastRefillMillis);
            tokens = Math.min(policy.capacity(), tokens + elapsed * policy.refillTokensPerSecond() / 1000.0);
            lastRefillMillis = now;
            if (tokens < 1) {
                return false;
            }
            tokens--;
            return true;
        }
    }
}
