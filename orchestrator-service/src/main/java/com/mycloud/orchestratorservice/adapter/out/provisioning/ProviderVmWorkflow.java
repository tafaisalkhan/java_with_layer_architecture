package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProvisionedResource;
import com.mycloud.orchestratorservice.domain.ResourceRequest;

public interface ProviderVmWorkflow {
    boolean supports(String providerType);

    ProviderSession login(ProviderConfiguration providerConfiguration, String userToken);

    String createVm(ProviderConfiguration providerConfiguration, ProviderSession session, ResourceRequest request);

    void assignVmAccess(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId);

    ProvisionedResource collectVmMetadata(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId);

    void rollbackVm(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId);
}
