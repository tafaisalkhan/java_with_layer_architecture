package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;

/**
 * Provider-specific authentication plugin. Adding a provider should require a new
 * implementation, not a change to a central provider-type switch.
 */
public interface ProviderAuthenticationStrategy {
    String providerType();

    ProviderSession authenticate(
        ProviderConfiguration providerConfiguration,
        String userToken,
        String endpointUrl
    );
}
