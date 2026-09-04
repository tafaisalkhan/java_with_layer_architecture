package com.mycloud.orchestratorservice.application.port.out.spi;

import java.util.UUID;

public interface UserTokenPort {
    String createToken(UUID customerId, UUID providerId);
}
