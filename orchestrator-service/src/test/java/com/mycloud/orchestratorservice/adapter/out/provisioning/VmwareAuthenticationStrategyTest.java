package com.mycloud.orchestratorservice.adapter.out.provisioning;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VmwareAuthenticationStrategyTest {
    @Test
    void usesOneConfiguredCredentialSessionPerProvider() {
        VmwareAuthenticationStrategy strategy = new VmwareAuthenticationStrategy("vmware-user", "secret", 300, 30);
        ProviderConfiguration provider = new ProviderConfiguration(
            UUID.randomUUID(), "VMware", ProviderTypes.VMWARE, "", "", List.of()
        );

        String first = strategy.authenticate(provider, "user-a", "https://vcenter.example").accessToken();
        String second = strategy.authenticate(provider, "user-b", "https://vcenter.example").accessToken();

        assertThat(second).isEqualTo(first);
    }
}
