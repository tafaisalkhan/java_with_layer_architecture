package com.mycloud.orchestratorservice.application.port.out.spi.dto;

import java.util.List;
import java.util.UUID;

public record ProviderConfiguration(
    UUID providerId,
    String name,
    String type,
    String adminUsername,
    String credentialSecretRef,
    List<ProviderEndpointView> endpoints
) {
    public String endpointUrl(String serviceName) {
        return endpoints.stream()
            .filter(endpoint -> endpoint.serviceName().equalsIgnoreCase(serviceName))
            .findFirst()
            .map(ProviderEndpointView::url)
            .orElseThrow(() -> new IllegalStateException("provider endpoint not configured: " + serviceName));
    }
}
