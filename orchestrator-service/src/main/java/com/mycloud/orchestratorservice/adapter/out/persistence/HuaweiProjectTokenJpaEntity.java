package com.mycloud.orchestratorservice.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "huawei_project_tokens")
public class HuaweiProjectTokenJpaEntity {
    @Id
    private UUID id;
    @Column(name = "provider_id", nullable = false)
    private UUID providerId;
    @Column(name = "project_key", nullable = false, unique = true)
    private String projectKey;
    @Column(name = "encrypted_access_token", nullable = false, length = 2048)
    private String encryptedAccessToken;
    @Column(name = "endpoint_url", nullable = false)
    private String endpointUrl;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Version
    private long version;

    protected HuaweiProjectTokenJpaEntity() {}

    public HuaweiProjectTokenJpaEntity(UUID id, UUID providerId, String projectKey, String encryptedAccessToken,
                                       String endpointUrl, Instant expiresAt, Instant updatedAt) {
        this.id = id;
        this.providerId = providerId;
        this.projectKey = projectKey;
        this.encryptedAccessToken = encryptedAccessToken;
        this.endpointUrl = endpointUrl;
        this.expiresAt = expiresAt;
        this.updatedAt = updatedAt;
    }

    public UUID getProviderId() { return providerId; }
    public String getProjectKey() { return projectKey; }
    public String getEncryptedAccessToken() { return encryptedAccessToken; }
    public String getEndpointUrl() { return endpointUrl; }
    public Instant getExpiresAt() { return expiresAt; }

    public void replaceToken(String encryptedAccessToken, String endpointUrl, Instant expiresAt, Instant updatedAt) {
        this.encryptedAccessToken = encryptedAccessToken;
        this.endpointUrl = endpointUrl;
        this.expiresAt = expiresAt;
        this.updatedAt = updatedAt;
    }
}
