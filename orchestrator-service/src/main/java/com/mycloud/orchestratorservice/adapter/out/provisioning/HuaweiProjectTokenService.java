package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.adapter.out.persistence.HuaweiProjectTokenJpaEntity;
import com.mycloud.orchestratorservice.adapter.out.persistence.repository.HuaweiProjectTokenRepository;
import com.mycloud.orchestratorservice.adapter.out.security.TokenCipher;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HuaweiProjectTokenService {
    private final HuaweiProjectTokenRepository repository;
    private final TokenCipher tokenCipher;
    private final Duration tokenLifetime;
    private final Duration refreshWindow;

    public HuaweiProjectTokenService(HuaweiProjectTokenRepository repository, TokenCipher tokenCipher,
        @Value("${app.huawei-token.token-lifetime-seconds:7200}") long tokenLifetimeSeconds,
        @Value("${app.huawei-token.refresh-before-expiry-seconds:600}") long refreshWindowSeconds) {
        this.repository = repository;
        this.tokenCipher = tokenCipher;
        this.tokenLifetime = Duration.ofSeconds(tokenLifetimeSeconds);
        this.refreshWindow = Duration.ofSeconds(refreshWindowSeconds);
    }

    @Transactional
    public synchronized ProviderSession sessionFor(ProviderConfiguration provider, String endpointUrl) {
        String projectKey = projectKey(provider);
        Instant now = Instant.now();
        HuaweiProjectTokenJpaEntity entity = repository.findByProjectKey(projectKey).orElse(null);
        if (entity == null || !entity.getExpiresAt().isAfter(now.plus(refreshWindow))) {
            entity = refresh(provider.providerId(), projectKey, endpointUrl, entity, now);
        }
        return new ProviderSession(tokenCipher.decrypt(entity.getEncryptedAccessToken()),
            entity.getEndpointUrl(), entity.getExpiresAt());
    }

    @Scheduled(cron = "${app.huawei-token.refresh-cron:0 0 * * * *}")
    @Transactional
    public void refreshStoredProjectTokens() {
        Instant now = Instant.now();
        for (HuaweiProjectTokenJpaEntity entity : repository.findAll()) {
            refresh(entity.getProviderId(), entity.getProjectKey(), entity.getEndpointUrl(), entity, now);
        }
    }

    private HuaweiProjectTokenJpaEntity refresh(UUID providerId, String projectKey, String endpointUrl,
                                                  HuaweiProjectTokenJpaEntity entity, Instant now) {
        // Replace this UUID with the token returned by Huawei IAM in production.
        String accessToken = "mock-huawei-" + UUID.randomUUID();
        String encrypted = tokenCipher.encrypt(accessToken);
        Instant expiresAt = now.plus(tokenLifetime);
        if (entity == null) {
            UUID id = UUID.nameUUIDFromBytes(projectKey.getBytes(StandardCharsets.UTF_8));
            entity = new HuaweiProjectTokenJpaEntity(id, providerId, projectKey, encrypted, endpointUrl, expiresAt, now);
        } else {
            entity.replaceToken(encrypted, endpointUrl, expiresAt, now);
        }
        return repository.save(entity);
    }

    private String projectKey(ProviderConfiguration provider) {
        return provider.providerId() + ":" + provider.credentialSecretRef();
    }
}
