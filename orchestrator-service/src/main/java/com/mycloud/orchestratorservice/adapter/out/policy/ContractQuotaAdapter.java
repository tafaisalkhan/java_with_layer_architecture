package com.mycloud.orchestratorservice.adapter.out.policy;

import com.mycloud.orchestratorservice.application.port.out.spi.QuotaManagementPort;
import com.mycloud.orchestratorservice.domain.ResourceRequest;
import com.mycloud.orchestratorservice.domain.ResourceType;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static com.mycloud.orchestratorservice.adapter.out.policy.ContractQuotaConstants.COMMIT_QUOTA_PATH;
import static com.mycloud.orchestratorservice.adapter.out.policy.ContractQuotaConstants.RELEASE_QUOTA_PATH;
import static com.mycloud.orchestratorservice.adapter.out.policy.ContractQuotaConstants.RESERVE_QUOTA_PATH;
import static com.mycloud.orchestratorservice.adapter.out.policy.ContractQuotaConstants.VM_PRODUCT_ID;

@Component
public class ContractQuotaAdapter implements QuotaManagementPort {
    private final RestClient restClient;

    public ContractQuotaAdapter(@Value("${app.contract-service.base-url}") String contractServiceBaseUrl) {
        this.restClient = RestClient.builder()
            .baseUrl(contractServiceBaseUrl)
            .build();
    }

    @Override
    public void ensureQuotaAvailable(UUID contractId, UUID customerId, ResourceType resourceType, ResourceRequest request) {
        postQuota(RESERVE_QUOTA_PATH, contractId, customerId, resourceType);
    }

    @Override
    public void commitQuota(UUID contractId, UUID customerId, ResourceType resourceType, ResourceRequest request) {
        postQuota(COMMIT_QUOTA_PATH, contractId, customerId, resourceType);
    }

    @Override
    public void releaseQuota(UUID contractId, UUID customerId, ResourceType resourceType, ResourceRequest request) {
        postQuota(RELEASE_QUOTA_PATH, contractId, customerId, resourceType);
    }

    private void postQuota(String uri, UUID contractId, UUID customerId, ResourceType resourceType) {
        restClient.post()
            .uri(uri)
            .body(new QuotaRequest(contractId, customerId, quotaProductId(resourceType), 1))
            .retrieve()
            .toBodilessEntity();
    }

    private UUID quotaProductId(ResourceType resourceType) {
        return switch (resourceType) {
            case VM -> VM_PRODUCT_ID;
            default -> throw new IllegalArgumentException("quota product is not mapped for resource type: " + resourceType);
        };
    }

    private record QuotaRequest(UUID contractId, UUID customerId, UUID productId, int quantity) {
    }
}
