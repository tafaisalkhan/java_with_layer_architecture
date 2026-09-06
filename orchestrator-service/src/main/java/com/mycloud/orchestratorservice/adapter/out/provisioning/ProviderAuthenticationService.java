package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ProviderAuthenticationService {
    private final Map<String, ProviderAuthenticationStrategy> strategies;

    public ProviderAuthenticationService(List<ProviderAuthenticationStrategy> strategies) {
        Map<String, ProviderAuthenticationStrategy> indexed = new HashMap<>();
        for (ProviderAuthenticationStrategy strategy : strategies) {
            String providerType = normalize(strategy.providerType());
            if (indexed.putIfAbsent(providerType, strategy) != null) {
                throw new IllegalStateException("multiple authentication strategies configured for: " + providerType);
            }
        }
        this.strategies = Map.copyOf(indexed);
    }

    public ProviderSession authenticate(
        ProviderConfiguration providerConfiguration,
        String userToken,
        String endpointUrl
    ) {
        ProviderAuthenticationStrategy strategy = strategies.get(normalize(providerConfiguration.type()));
        if (strategy == null) {
            throw new IllegalArgumentException(
                "no authentication strategy configured for provider type: " + providerConfiguration.type()
            );
        }
        return strategy.authenticate(providerConfiguration, userToken, endpointUrl);
    }

    private static String normalize(String providerType) {
        if (providerType == null || providerType.isBlank()) {
            throw new IllegalArgumentException("provider type must not be blank");
        }
        return providerType.trim().toUpperCase(Locale.ROOT);
    }
}
