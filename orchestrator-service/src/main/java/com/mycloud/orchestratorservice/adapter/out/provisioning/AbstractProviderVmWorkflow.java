package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProvisionedResource;
import java.util.Map;

import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.MANAGEMENT_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.PROVIDER_ID;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.PROVIDER_TYPE;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.RESOURCE_ID;

public abstract class AbstractProviderVmWorkflow implements ProviderVmWorkflow {
    @Override
    public ProvisionedResource collectVmMetadata(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        Map<String, String> metadata = resourceMetadata(providerConfiguration, session, resourceId);
        return new ProvisionedResource(
            resourceId,
            providerType(),
            metadata.get(MANAGEMENT_URL),
            metadata
        );
    }

    @Override
    public boolean supports(String providerType) {
        return providerType().equalsIgnoreCase(providerType);
    }

    protected abstract String providerType();

    @Override
    public void assignVmAccess(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        // Providers override when access assignment needs explicit API calls.
    }

    @Override
    public void rollbackVm(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        // Providers override with terminate/delete VM calls.
    }

    protected Map<String, String> resourceMetadata(
        ProviderConfiguration providerConfiguration,
        ProviderSession session,
        String resourceId
    ) {
        return Map.of(
            RESOURCE_ID, resourceId,
            PROVIDER_ID, providerConfiguration.providerId().toString(),
            PROVIDER_TYPE, providerType()
        );
    }
}
