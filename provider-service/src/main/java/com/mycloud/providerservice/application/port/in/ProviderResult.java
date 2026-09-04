package com.mycloud.providerservice.application.port.in;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ProviderResult(
    UUID providerId,
    String name,
    String type,
    String status,
    String adminUsername,
    String credentialSecretRef,
    List<ProviderEndpointResult> endpoints,
    Set<UUID> allowedCustomerIds
) {
}
