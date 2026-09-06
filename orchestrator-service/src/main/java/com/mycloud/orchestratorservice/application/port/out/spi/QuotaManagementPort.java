package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.domain.ResourceRequest;
import com.mycloud.orchestratorservice.domain.ResourceType;
import java.util.UUID;

public interface QuotaManagementPort {
    void ensureQuotaAvailable(UUID contractId, UUID customerId, ResourceType resourceType, ResourceRequest request);

    void commitQuota(UUID contractId, UUID customerId, ResourceType resourceType, ResourceRequest request);

    void releaseQuota(UUID contractId, UUID customerId, ResourceType resourceType, ResourceRequest request);
}
