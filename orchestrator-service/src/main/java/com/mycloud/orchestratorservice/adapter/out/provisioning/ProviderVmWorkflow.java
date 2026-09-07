package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProvisionedResource;
import com.mycloud.orchestratorservice.domain.ResourceRequest;
import com.mycloud.orchestratorservice.domain.ProvisioningStatus;

public interface ProviderVmWorkflow {
    String providerType();

    ProviderSession createScopedSession(ProviderConfiguration providerConfiguration, String unscopedToken);

    String createVm(ProviderConfiguration providerConfiguration, ProviderSession session, ResourceRequest request);

    ProvisioningStatus getVmStatus(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId);

    void assignVmAccess(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId);

    ProvisionedResource collectVmMetadata(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId);

    void rollbackVm(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId);
}
