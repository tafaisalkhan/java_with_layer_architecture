package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import java.util.UUID;

public interface ProviderConfigurationPort {
    ProviderConfiguration getAllowedProvider(UUID customerId, UUID providerId);
}
