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
public class VmwareAuthenticationStrategy implements ProviderAuthenticationStrategy {
    private final String username;
    private final String password;
    private final Duration tokenLifetime;
    private final Duration refreshWindow;
    private final ConcurrentMap<UUID, ProviderSession> sessions = new ConcurrentHashMap<>();

    public VmwareAuthenticationStrategy(
        @Value("${app.mock-provider-credentials.vmware.username:administrator@vsphere.local}") String username,
        @Value("${app.mock-provider-credentials.vmware.password:vmware-demo-secret}") String password,
        @Value("${app.provider-session.token-lifetime-seconds:300}") long tokenLifetimeSeconds,
        @Value("${app.provider-session.refresh-before-expiry-seconds:30}") long refreshBeforeExpirySeconds
    ) {
        this.username = username;
        this.password = password;
        this.tokenLifetime = Duration.ofSeconds(tokenLifetimeSeconds);
        this.refreshWindow = Duration.ofSeconds(refreshBeforeExpirySeconds);
        validateConfiguration();
    }

    @Override
    public String providerType() {
        return ProviderTypes.VMWARE;
    }

    @Override
    public ProviderSession authenticate(ProviderConfiguration provider, String userToken, String endpointUrl) {
        Instant now = Instant.now();
        return sessions.compute(provider.providerId(), (ignored, current) -> {
            if (current != null && current.endpointUrl().equals(endpointUrl)
                && !current.expiresWithin(refreshWindow, now)) {
                return current;
            }
            // Replace with the vCenter session returned for the configured account.
            return new ProviderSession("mock-vmware-" + UUID.randomUUID(), endpointUrl, now.plus(tokenLifetime));
        });
    }

    private void validateConfiguration() {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException("VMware credentials are not configured");
        }
        if (tokenLifetime.isZero() || tokenLifetime.isNegative() || refreshWindow.isNegative()
            || refreshWindow.compareTo(tokenLifetime) >= 0) {
            throw new IllegalArgumentException("provider token lifetime must be positive and greater than refresh window");
        }
    }
}
