package com.mycloud.orchestratorservice.adapter.out.provisioning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mycloud.orchestratorservice.adapter.out.persistence.HuaweiProjectTokenJpaEntity;
import com.mycloud.orchestratorservice.adapter.out.persistence.repository.HuaweiProjectTokenRepository;
import com.mycloud.orchestratorservice.adapter.out.security.TokenCipher;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderEndpointView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class HuaweiProjectTokenServiceTest {
    @Test
    void storesEncryptedTokenAndReusesItForTheSameProject() {
        HuaweiProjectTokenRepository repository = mock(HuaweiProjectTokenRepository.class);
        AtomicReference<HuaweiProjectTokenJpaEntity> stored = new AtomicReference<>();
        when(repository.findByProjectKey(any())).thenAnswer(invocation -> Optional.ofNullable(stored.get()));
        when(repository.save(any())).thenAnswer(invocation -> {
            HuaweiProjectTokenJpaEntity entity = invocation.getArgument(0);
            stored.set(entity);
            return entity;
        });
        TokenCipher cipher = new TokenCipher("unit-test-encryption-key");
        HuaweiProjectTokenService service = new HuaweiProjectTokenService(repository, cipher, 7200, 600);
        ProviderConfiguration provider = new ProviderConfiguration(
            UUID.randomUUID(), "Huawei project", ProviderTypes.HUAWEI, "admin", "secret/huawei/project-a",
            List.of(new ProviderEndpointView("iam", "https://iam.example.test"))
        );

        var first = service.sessionFor(provider, "https://iam.example.test");
        var second = service.sessionFor(provider, "https://iam.example.test");

        assertThat(second.accessToken()).isEqualTo(first.accessToken());
        assertThat(stored.get().getEncryptedAccessToken()).doesNotContain(first.accessToken());
        assertThat(cipher.decrypt(stored.get().getEncryptedAccessToken())).isEqualTo(first.accessToken());
    }
}
