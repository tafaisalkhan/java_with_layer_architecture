package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
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

    public MockProviderCredentialAuthenticator(
        @Value("${app.mock-provider-credentials.openstack.username:openstack-admin}") String openStackUsername,
        @Value("${app.mock-provider-credentials.openstack.password:openstack-demo-secret}") String openStackPassword,
        @Value("${app.mock-provider-credentials.vmware.username:administrator@vsphere.local}") String vmwareUsername,
        @Value("${app.mock-provider-credentials.vmware.password:vmware-demo-secret}") String vmwarePassword,
        @Value("${app.mock-provider-credentials.huawei.username:huawei-admin}") String huaweiUsername,
        @Value("${app.mock-provider-credentials.huawei.password:huawei-demo-secret}") String huaweiPassword
    ) {
        this.openStack = new Credential(openStackUsername, openStackPassword);
        this.vmware = new Credential(vmwareUsername, vmwarePassword);
        this.huawei = new Credential(huaweiUsername, huaweiPassword);
    }

    public ProviderSession authenticate(ProviderConfiguration provider, String userToken, String endpointUrl) {
        Credential credential = credentialFor(provider.type());
        if (credential.username().isBlank() || credential.password().isBlank()) {
            throw new IllegalStateException("mock credentials are not configured for provider type: " + provider.type());
        }
        // Deterministic per user/provider credentials, without exposing the password itself.
        String source = provider.providerId() + ":" + credential.username() + ":" + credential.password() + ":" + userToken;
        String sessionToken = "mock-" + provider.type().toLowerCase(Locale.ROOT) + "-" +
            UUID.nameUUIDFromBytes(source.getBytes(StandardCharsets.UTF_8));
        return new ProviderSession(sessionToken, endpointUrl);
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
