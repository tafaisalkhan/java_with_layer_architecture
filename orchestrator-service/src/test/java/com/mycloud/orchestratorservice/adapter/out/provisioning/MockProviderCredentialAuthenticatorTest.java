package com.mycloud.orchestratorservice.adapter.out.provisioning;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderEndpointView;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MockProviderCredentialAuthenticatorTest {
    private final MockProviderCredentialAuthenticator authenticator = new MockProviderCredentialAuthenticator(
        "openstack-user", "openstack-password",
        "vmware-user", "vmware-password",
        "huawei-user", "huawei-password",
        300, 30
    );

    @ParameterizedTest
    @ValueSource(strings = {ProviderTypes.OPENSTACK, ProviderTypes.VMWARE, ProviderTypes.HUAWEI})
    void createsProviderSpecificMockSessionWithoutExposingPassword(String providerType) {
        ProviderConfiguration provider = new ProviderConfiguration(
            UUID.randomUUID(), "provider", providerType, "admin", "secret/mock",
            List.of(new ProviderEndpointView("identity", "https://provider.example.test"))
        );

        var session = authenticator.authenticate(provider, "customer-token", "https://provider.example.test");

        assertThat(session.accessToken()).startsWith("mock-" + providerType.toLowerCase() + "-");
        assertThat(session.accessToken()).doesNotContain("password");
        assertThat(session.endpointUrl()).isEqualTo("https://provider.example.test");
        assertThat(session.expiresAt()).isAfter(java.time.Instant.now().plusSeconds(250));
    }

    @org.junit.jupiter.api.Test
    void reusesOneValidSessionForConcurrentPollingRequests() throws Exception {
        ProviderConfiguration provider = new ProviderConfiguration(
            UUID.randomUUID(), "provider", ProviderTypes.OPENSTACK, "admin", "secret/mock",
            List.of(new ProviderEndpointView("identity", "https://provider.example.test"))
        );
        try (var executor = java.util.concurrent.Executors.newFixedThreadPool(10)) {
            var calls = java.util.stream.IntStream.range(0, 100)
                .mapToObj(index -> (java.util.concurrent.Callable<String>) () ->
                    authenticator.authenticate(provider, "customer-token", "https://provider.example.test").accessToken())
                .toList();

            var tokens = executor.invokeAll(calls).stream()
                .map(future -> {
                    try { return future.get(); }
                    catch (Exception exception) { throw new RuntimeException(exception); }
                })
                .collect(java.util.stream.Collectors.toSet());

            assertThat(tokens).hasSize(1);
        }
    }
}
