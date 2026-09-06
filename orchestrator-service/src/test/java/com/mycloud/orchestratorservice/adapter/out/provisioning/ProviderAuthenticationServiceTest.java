package com.mycloud.orchestratorservice.adapter.out.provisioning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProviderAuthenticationServiceTest {
    @Test
    void delegatesToAPluginWithoutChangingTheRegistry() {
        ProviderAuthenticationStrategy customStrategy = new ProviderAuthenticationStrategy() {
            @Override
            public String providerType() {
                return "CUSTOM_CLOUD";
            }

            @Override
            public ProviderSession authenticate(ProviderConfiguration provider, String userToken, String endpointUrl) {
                return new ProviderSession("custom-session", endpointUrl, Instant.now().plusSeconds(60));
            }
        };
        ProviderAuthenticationService service = new ProviderAuthenticationService(List.of(customStrategy));
        ProviderConfiguration provider = new ProviderConfiguration(
            UUID.randomUUID(), "custom", "custom_cloud", "", "", List.of()
        );

        assertThat(service.authenticate(provider, "user-token", "https://identity.example").accessToken())
            .isEqualTo("custom-session");
    }

    @Test
    void rejectsDuplicateStrategiesForTheSameProviderType() {
        ProviderAuthenticationStrategy first = strategy("DUPLICATE");
        ProviderAuthenticationStrategy second = strategy("duplicate");

        assertThatThrownBy(() -> new ProviderAuthenticationService(List.of(first, second)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("DUPLICATE");
    }

    private ProviderAuthenticationStrategy strategy(String type) {
        return new ProviderAuthenticationStrategy() {
            public String providerType() { return type; }
            public ProviderSession authenticate(ProviderConfiguration provider, String token, String endpoint) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
