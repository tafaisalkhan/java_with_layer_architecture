package com.mycloud.orchestratorservice.adapter.out.http;

import com.mycloud.orchestratorservice.application.port.out.spi.ProviderConfigurationPort;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderEndpointView;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProviderConfigurationHttpAdapter implements ProviderConfigurationPort {
    private final RestClient restClient;

    public ProviderConfigurationHttpAdapter(@Value("${app.provider-service.base-url}") String providerServiceBaseUrl) {
        this.restClient = RestClient.builder()
            .baseUrl(providerServiceBaseUrl)
            .build();
    }

    @Override
    public ProviderConfiguration getAllowedProvider(UUID customerId, UUID providerId) {
        ProviderResponse response = restClient.get()
            .uri("/providers/{providerId}", providerId)
            .retrieve()
            .body(ProviderResponse.class);
        if (response == null) {
            throw new IllegalStateException("provider-service returned empty provider");
        }
        if (!response.allowedCustomerIds().contains(customerId)) {
            throw new IllegalStateException("provider is not allowed for customer: " + customerId);
        }
        return new ProviderConfiguration(
            response.providerId(),
            response.name(),
            response.type(),
            response.adminUsername(),
            response.credentialSecretRef(),
            response.endpoints()
        );
    }

    private record ProviderResponse(
        UUID providerId,
        String name,
        String type,
        String status,
        String adminUsername,
        String credentialSecretRef,
        List<ProviderEndpointView> endpoints,
        Set<UUID> allowedCustomerIds
    ) {
    }
}
