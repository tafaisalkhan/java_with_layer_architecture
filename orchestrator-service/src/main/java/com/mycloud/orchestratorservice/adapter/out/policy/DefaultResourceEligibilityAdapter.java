package com.mycloud.orchestratorservice.adapter.out.policy;

import com.mycloud.orchestratorservice.application.port.out.spi.ResourceEligibilityPort;
import com.mycloud.orchestratorservice.domain.ResourceRequest;
import com.mycloud.orchestratorservice.domain.ResourceType;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DefaultResourceEligibilityAdapter implements ResourceEligibilityPort {
    @Override
    public void ensureEligible(UUID customerId, UUID providerId, ResourceType resourceType, ResourceRequest request) {
        if (resourceType != ResourceType.VM) {
            throw new IllegalArgumentException("unsupported resource type: " + resourceType);
        }
    }
}
