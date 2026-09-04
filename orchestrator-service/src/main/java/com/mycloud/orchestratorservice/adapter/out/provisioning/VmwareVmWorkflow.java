package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import com.mycloud.orchestratorservice.domain.ResourceRequest;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderEndpointNames.NSX;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderEndpointNames.VCENTER;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderTypes.VMWARE;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.MANAGEMENT_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.NETWORK_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.PROVIDER_ID;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.PROVIDER_TYPE;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.RESOURCE_ID;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.VCENTER_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ResourceIdPrefixes.VMWARE_VM;

@Component
public class VmwareVmWorkflow extends AbstractProviderVmWorkflow {
    @Override
    protected String providerType() {
        return VMWARE;
    }

    @Override
    public ProviderSession login(ProviderConfiguration providerConfiguration, String userToken) {
        return new ProviderSession(userToken, providerConfiguration.endpointUrl(VCENTER));
    }

    @Override
    public String createVm(ProviderConfiguration providerConfiguration, ProviderSession session, ResourceRequest request) {
        return VMWARE_VM + UUID.randomUUID();
    }

    @Override
    public void assignVmAccess(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        providerConfiguration.endpointUrl(NSX);
    }

    @Override
    public void rollbackVm(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        providerConfiguration.endpointUrl(VCENTER);
    }

    @Override
    protected Map<String, String> resourceMetadata(
        ProviderConfiguration providerConfiguration,
        ProviderSession session,
        String resourceId
    ) {
        return Map.of(
            RESOURCE_ID, resourceId,
            PROVIDER_ID, providerConfiguration.providerId().toString(),
            PROVIDER_TYPE, providerType(),
            VCENTER_URL, session.endpointUrl(),
            NETWORK_URL, providerConfiguration.endpointUrl(NSX),
            MANAGEMENT_URL, session.endpointUrl()
        );
    }
}
