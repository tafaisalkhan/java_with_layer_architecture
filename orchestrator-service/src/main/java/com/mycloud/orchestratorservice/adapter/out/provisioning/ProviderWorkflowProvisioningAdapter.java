package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.ResourceProvisioningPort;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProvisionedResource;
import com.mycloud.orchestratorservice.domain.ResourceRequest;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProviderWorkflowProvisioningAdapter implements ResourceProvisioningPort {
    private final List<ProviderVmWorkflow> workflows;

    public ProviderWorkflowProvisioningAdapter(List<ProviderVmWorkflow> workflows) {
        this.workflows = workflows;
    }

    @Override
    public ProviderSession login(ProviderConfiguration providerConfiguration, String userToken) {
        return workflowFor(providerConfiguration).login(providerConfiguration, userToken);
    }

    @Override
    public String createVm(ProviderConfiguration providerConfiguration, ProviderSession session, ResourceRequest request) {
        return workflowFor(providerConfiguration).createVm(providerConfiguration, session, request);
    }

    @Override
    public void assignVmAccess(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        workflowFor(providerConfiguration).assignVmAccess(providerConfiguration, session, resourceId);
    }

    @Override
    public ProvisionedResource collectVmMetadata(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        return workflowFor(providerConfiguration).collectVmMetadata(providerConfiguration, session, resourceId);
    }

    @Override
    public void rollbackVm(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        workflowFor(providerConfiguration).rollbackVm(providerConfiguration, session, resourceId);
    }

    private ProviderVmWorkflow workflowFor(ProviderConfiguration providerConfiguration) {
        return workflows.stream()
            .filter(workflow -> workflow.supports(providerConfiguration.type()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("unsupported provider type: " + providerConfiguration.type()));
    }
}
