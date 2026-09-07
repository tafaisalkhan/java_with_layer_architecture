package com.mycloud.orchestratorservice.application.port.out.spi;

import java.util.UUID;

public interface UserTokenPort {
    String createUnscopedToken(UUID customerId, UUID providerId);
}
