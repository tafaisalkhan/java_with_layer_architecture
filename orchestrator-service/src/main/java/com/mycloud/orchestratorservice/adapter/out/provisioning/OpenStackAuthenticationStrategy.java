package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OpenStackAuthenticationStrategy implements ProviderAuthenticationStrategy {
    private final Duration tokenLifetime;
    private final Duration refreshWindow;
    private final ConcurrentMap<SessionScope, ProviderSession> sessions = new ConcurrentHashMap<>();

    public OpenStackAuthenticationStrategy(
        @Value("${app.provider-session.token-lifetime-seconds:300}") long tokenLifetimeSeconds,
        @Value("${app.provider-session.refresh-before-expiry-seconds:30}") long refreshBeforeExpirySeconds
    ) {
        this.tokenLifetime = Duration.ofSeconds(tokenLifetimeSeconds);
        this.refreshWindow = Duration.ofSeconds(refreshBeforeExpirySeconds);
        validateDurations();
    }

    @Override
    public String providerType() {
        return ProviderTypes.OPENSTACK;
    }

    @Override
    public ProviderSession authenticate(ProviderConfiguration provider, String userToken, String endpointUrl) {
        if (userToken == null || userToken.isBlank()) {
            throw new IllegalArgumentException("an OpenStack user/project token is required");
        }
        Instant now = Instant.now();
        SessionScope scope = new SessionScope(provider.providerId(), userToken);
        return sessions.compute(scope, (ignored, current) -> {
            if (current != null && current.endpointUrl().equals(endpointUrl)
                && !current.expiresWithin(refreshWindow, now)) {
                return current;
            }
            // Replace with the Keystone token returned for this user's project.
            return new ProviderSession("mock-openstack-" + UUID.randomUUID(), endpointUrl, now.plus(tokenLifetime));
        });
    }

    private void validateDurations() {
        if (tokenLifetime.isZero() || tokenLifetime.isNegative() || refreshWindow.isNegative()
            || refreshWindow.compareTo(tokenLifetime) >= 0) {
            throw new IllegalArgumentException("provider token lifetime must be positive and greater than refresh window");
        }
    }

    private record SessionScope(UUID providerId, String userProjectToken) {
    }
}
