package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Development-only credential resolver. Passwords come from environment-backed
 * configuration and are never stored in an operation or returned by the API.
 */
@Component
public class MockProviderCredentialAuthenticator {
    private final Credential openStack;
    private final Credential vmware;
    private final Credential huawei;
    private final Duration tokenLifetime;
    private final Duration refreshWindow;
    private final ConcurrentMap<UUID, ProviderSession> sessions = new ConcurrentHashMap<>();

    public MockProviderCredentialAuthenticator(
        @Value("${app.mock-provider-credentials.openstack.username:openstack-admin}") String openStackUsername,
        @Value("${app.mock-provider-credentials.openstack.password:openstack-demo-secret}") String openStackPassword,
        @Value("${app.mock-provider-credentials.vmware.username:administrator@vsphere.local}") String vmwareUsername,
        @Value("${app.mock-provider-credentials.vmware.password:vmware-demo-secret}") String vmwarePassword,
        @Value("${app.mock-provider-credentials.huawei.username:huawei-admin}") String huaweiUsername,
        @Value("${app.mock-provider-credentials.huawei.password:huawei-demo-secret}") String huaweiPassword,
        @Value("${app.provider-session.token-lifetime-seconds:300}") long tokenLifetimeSeconds,
        @Value("${app.provider-session.refresh-before-expiry-seconds:30}") long refreshBeforeExpirySeconds
    ) {
        this.openStack = new Credential(openStackUsername, openStackPassword);
        this.vmware = new Credential(vmwareUsername, vmwarePassword);
        this.huawei = new Credential(huaweiUsername, huaweiPassword);
        this.tokenLifetime = Duration.ofSeconds(tokenLifetimeSeconds);
        this.refreshWindow = Duration.ofSeconds(refreshBeforeExpirySeconds);
        if (tokenLifetime.isZero() || tokenLifetime.isNegative() || refreshWindow.isNegative()
            || refreshWindow.compareTo(tokenLifetime) >= 0) {
            throw new IllegalArgumentException("provider token lifetime must be positive and greater than refresh window");
        }
    }

    public ProviderSession authenticate(ProviderConfiguration provider, String userToken, String endpointUrl) {
        Instant now = Instant.now();
        return sessions.compute(provider.providerId(), (providerId, current) -> {
            if (current != null && current.endpointUrl().equals(endpointUrl)
                && !current.expiresWithin(refreshWindow, now)) {
                return current;
            }
            return createSession(provider, userToken, endpointUrl, now);
        });
    }

    public void invalidate(UUID providerId, String rejectedAccessToken) {
        sessions.computeIfPresent(providerId, (id, current) ->
            current.accessToken().equals(rejectedAccessToken) ? null : current);
    }

    private ProviderSession createSession(
        ProviderConfiguration provider,
        String userToken,
        String endpointUrl,
        Instant now
    ) {
        Credential credential = credentialFor(provider.type());
        if (credential.username().isBlank() || credential.password().isBlank()) {
            throw new IllegalStateException("mock credentials are not configured for provider type: " + provider.type());
        }
        // This simulates the token returned by a provider login. Neither provider
        // password nor the incoming user token is embedded in the access token.
        String sessionToken = "mock-" + provider.type().toLowerCase(Locale.ROOT) + "-" + UUID.randomUUID();
        return new ProviderSession(sessionToken, endpointUrl, now.plus(tokenLifetime));
    }

    private Credential credentialFor(String providerType) {
        return switch (providerType.toUpperCase(Locale.ROOT)) {
            case ProviderTypes.OPENSTACK -> openStack;
            case ProviderTypes.VMWARE -> vmware;
            case ProviderTypes.HUAWEI -> huawei;
            default -> throw new IllegalArgumentException("unsupported provider type: " + providerType);
        };
    }

    private record Credential(String username, String password) {
    }
}
