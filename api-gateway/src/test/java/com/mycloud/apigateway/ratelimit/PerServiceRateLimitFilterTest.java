package com.mycloud.apigateway.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

class PerServiceRateLimitFilterTest {
    @Test
    void limitsEachServiceIndependently() {
        GatewayRateLimitProperties properties = new GatewayRateLimitProperties(Map.of(
            "payments", new GatewayRateLimitProperties.RateLimit(1, 1),
            "products", new GatewayRateLimitProperties.RateLimit(1, 1)
        ));
        PerServiceRateLimitFilter filter = new PerServiceRateLimitFilter(
            properties, Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC));

        assertThat(invoke(filter, "/api/payments/1")).isTrue();
        MockServerWebExchange limited = exchange("/api/payments/2");
        filter.filter(limited, ignored -> reactor.core.publisher.Mono.empty()).block();
        assertThat(limited.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(invoke(filter, "/api/products/1")).isTrue();
    }

    private boolean invoke(PerServiceRateLimitFilter filter, String path) {
        AtomicBoolean called = new AtomicBoolean();
        filter.filter(exchange(path), ignored -> {
            called.set(true);
            return reactor.core.publisher.Mono.empty();
        }).block();
        return called.get();
    }

    private MockServerWebExchange exchange(String path) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(path)
            .remoteAddress(new java.net.InetSocketAddress("127.0.0.1", 1234)).build());
    }
}
