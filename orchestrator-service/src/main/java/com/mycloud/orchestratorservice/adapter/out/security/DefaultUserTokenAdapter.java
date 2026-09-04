package com.mycloud.orchestratorservice.adapter.out.security;

import com.mycloud.orchestratorservice.application.port.out.spi.UserTokenPort;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserTokenAdapter implements UserTokenPort {
    @Override
    public String createToken(UUID customerId, UUID providerId) {
        return "token-" + customerId + "-" + providerId;
    }
}
