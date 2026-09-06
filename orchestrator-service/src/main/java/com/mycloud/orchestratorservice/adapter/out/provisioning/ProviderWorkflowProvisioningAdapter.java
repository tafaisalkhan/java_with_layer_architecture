package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.ResourceProvisioningPort;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProvisionedResource;
import com.mycloud.orchestratorservice.domain.ResourceRequest;
import com.mycloud.orchestratorservice.domain.ProvisioningStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ProviderWorkflowProvisioningAdapter implements ResourceProvisioningPort {
    private final Map<String, ProviderVmWorkflow> workflows;

    public ProviderWorkflowProvisioningAdapter(List<ProviderVmWorkflow> workflows) {
        Map<String, ProviderVmWorkflow> indexed = new HashMap<>();
        for (ProviderVmWorkflow workflow : workflows) {
            String providerType = normalize(workflow.providerType());
            if (indexed.putIfAbsent(providerType, workflow) != null) {
                throw new IllegalStateException("multiple VM workflows configured for: " + providerType);
            }
        }
        this.workflows = Map.copyOf(indexed);
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
    public ProvisioningStatus getVmStatus(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        return workflowFor(providerConfiguration).getVmStatus(providerConfiguration, session, resourceId);
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
        ProviderVmWorkflow workflow = workflows.get(normalize(providerConfiguration.type()));
        if (workflow == null) {
            throw new IllegalArgumentException("unsupported provider type: " + providerConfiguration.type());
        }
        return workflow;
    }

    private static String normalize(String providerType) {
        if (providerType == null || providerType.isBlank()) {
            throw new IllegalArgumentException("provider type must not be blank");
        }
        return providerType.trim().toUpperCase(Locale.ROOT);
    }
}
