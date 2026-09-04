package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.domain.ResourceRequest;
import com.mycloud.orchestratorservice.domain.ResourceType;
import java.util.UUID;

public interface ResourceEligibilityPort {
    void ensureEligible(UUID customerId, UUID providerId, ResourceType resourceType, ResourceRequest request);
}
