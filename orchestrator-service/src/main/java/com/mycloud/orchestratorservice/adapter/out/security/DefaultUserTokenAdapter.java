package com.mycloud.orchestratorservice.adapter.out.security;

import com.mycloud.orchestratorservice.application.port.out.spi.UserTokenPort;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserTokenAdapter implements UserTokenPort {
    private final ConcurrentMap<UserProject, String> tokens = new ConcurrentHashMap<>();

    @Override
    public String createToken(UUID customerId, UUID providerId) {
        if (customerId == null || providerId == null) {
            throw new IllegalArgumentException("customerId and providerId are required to create a user token");
        }
        return tokens.computeIfAbsent(new UserProject(customerId, providerId), ignored ->
            "user-project-" + UUID.randomUUID()
        );
    }

    private record UserProject(UUID customerId, UUID providerId) {
    }
}
