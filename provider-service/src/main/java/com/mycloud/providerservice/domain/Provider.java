package com.mycloud.providerservice.domain;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record Provider(
    UUID id,
    String name,
    ProviderType type,
    ProviderStatus status,
    String adminUsername,
    String credentialSecretRef,
    List<ProviderEndpoint> endpoints,
    Set<UUID> allowedCustomerIds
) {
    public Provider {
        Objects.requireNonNull(id, "id must not be null");
        requireText(name, "name");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(status, "status must not be null");
        requireText(adminUsername, "adminUsername");
        requireText(credentialSecretRef, "credentialSecretRef");
        Objects.requireNonNull(endpoints, "endpoints must not be null");
        Objects.requireNonNull(allowedCustomerIds, "allowedCustomerIds must not be null");
        if (endpoints.isEmpty()) {
            throw new IllegalArgumentException("provider must contain at least one endpoint");
        }
        endpoints = List.copyOf(endpoints);
        allowedCustomerIds = Set.copyOf(allowedCustomerIds);
    }

    public static Provider create(
        String name,
        ProviderType type,
        String adminUsername,
        String credentialSecretRef,
        List<ProviderEndpoint> endpoints
    ) {
        return new Provider(
            UUID.randomUUID(),
            name,
            type,
            ProviderStatus.ACTIVE,
            adminUsername,
            credentialSecretRef,
            endpoints,
            Set.of()
        );
    }

    public Provider allowCustomer(UUID customerId) {
        Objects.requireNonNull(customerId, "customerId must not be null");
        java.util.HashSet<UUID> customers = new java.util.HashSet<>(allowedCustomerIds);
        customers.add(customerId);
        return new Provider(id, name, type, status, adminUsername, credentialSecretRef, endpoints, customers);
    }

    public boolean isAllowedFor(UUID customerId) {
        return allowedCustomerIds.contains(customerId);
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
