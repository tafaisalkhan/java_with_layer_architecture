package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.domain.ResourceRequest;
import com.mycloud.orchestratorservice.domain.ResourceType;
import java.util.UUID;

public interface QuotaManagementPort {
    void ensureQuotaAvailable(UUID customerId, ResourceType resourceType, ResourceRequest request);

    void commitQuota(UUID customerId, ResourceType resourceType, ResourceRequest request);

    void releaseQuota(UUID customerId, ResourceType resourceType, ResourceRequest request);
}
