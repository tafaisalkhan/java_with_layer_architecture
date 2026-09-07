package com.mycloud.orchestratorservice.adapter.out.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class DefaultUserTokenAdapterTest {
    private final DefaultUserTokenAdapter adapter = new DefaultUserTokenAdapter();

    @Test
    void createsOneOpaqueTokenPerUserAndProject() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        String token = adapter.createUnscopedToken(userId, projectId);

        assertThat(adapter.createUnscopedToken(userId, projectId)).isEqualTo(token);
        assertThat(adapter.createUnscopedToken(UUID.randomUUID(), projectId)).isNotEqualTo(token);
        assertThat(adapter.createUnscopedToken(userId, UUID.randomUUID())).isNotEqualTo(token);
        assertThat(token).doesNotContain(userId.toString()).doesNotContain(projectId.toString());
    }
}
