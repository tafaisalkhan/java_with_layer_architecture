package com.mycloud.orchestratorservice.adapter.out.provisioning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OpenStackAuthenticationStrategyTest {
    private final OpenStackAuthenticationStrategy strategy = new OpenStackAuthenticationStrategy(300, 30);
    private final ProviderConfiguration provider = new ProviderConfiguration(
        UUID.randomUUID(), "OpenStack", ProviderTypes.OPENSTACK, "", "", List.of()
    );

    @Test
    void isolatesSessionsByUserProjectToken() {
        String first = strategy.authenticate(provider, "user-project-a", "https://keystone.example").accessToken();
        String reused = strategy.authenticate(provider, "user-project-a", "https://keystone.example").accessToken();
        String second = strategy.authenticate(provider, "user-project-b", "https://keystone.example").accessToken();

        assertThat(reused).isEqualTo(first);
        assertThat(second).isNotEqualTo(first);
    }

    @Test
    void requiresAUserProjectToken() {
        assertThatThrownBy(() -> strategy.authenticate(provider, " ", "https://keystone.example"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("user/project token");
    }
}
