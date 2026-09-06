package com.mycloud.orchestratorservice.application.port.out.spi.dto;

import java.time.Duration;
import java.time.Instant;

public record ProviderSession(
    String accessToken,
    String endpointUrl,
    Instant expiresAt
) {
    public boolean expiresWithin(Duration refreshWindow, Instant now) {
        return expiresAt == null || !expiresAt.isAfter(now.plus(refreshWindow));
    }
}
