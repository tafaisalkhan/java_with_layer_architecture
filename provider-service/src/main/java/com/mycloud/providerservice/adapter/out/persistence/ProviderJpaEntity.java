package com.mycloud.providerservice.adapter.out.persistence;

import com.mycloud.providerservice.domain.ProviderStatus;
import com.mycloud.providerservice.domain.ProviderType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "providers")
public class ProviderJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private ProviderType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ProviderStatus status;

    @Column(name = "admin_username", nullable = false)
    private String adminUsername;

    @Column(name = "credential_secret_ref", nullable = false)
    private String credentialSecretRef;

    @ElementCollection
    @CollectionTable(name = "provider_endpoints", joinColumns = @JoinColumn(name = "provider_id"))
    private List<ProviderEndpointJpaEmbeddable> endpoints = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "provider_allowed_customers", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "customer_id", nullable = false)
    private Set<UUID> allowedCustomerIds = new HashSet<>();

    protected ProviderJpaEntity() {
    }

    public ProviderJpaEntity(
        UUID id,
        String name,
        ProviderType type,
        ProviderStatus status,
        String adminUsername,
        String credentialSecretRef,
        List<ProviderEndpointJpaEmbeddable> endpoints,
        Set<UUID> allowedCustomerIds
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.adminUsername = adminUsername;
        this.credentialSecretRef = credentialSecretRef;
        this.endpoints = new ArrayList<>(endpoints);
        this.allowedCustomerIds = new HashSet<>(allowedCustomerIds);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProviderType getType() {
        return type;
    }

    public ProviderStatus getStatus() {
        return status;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public String getCredentialSecretRef() {
        return credentialSecretRef;
    }

    public List<ProviderEndpointJpaEmbeddable> getEndpoints() {
        return endpoints;
    }

    public Set<UUID> getAllowedCustomerIds() {
        return allowedCustomerIds;
    }
}
