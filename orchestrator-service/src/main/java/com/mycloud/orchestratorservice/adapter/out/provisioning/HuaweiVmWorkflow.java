package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import com.mycloud.orchestratorservice.domain.ResourceRequest;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderEndpointNames.CONSOLE;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderEndpointNames.ECS;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderEndpointNames.IAM;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderEndpointNames.VPC;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderTypes.HUAWEI;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.ECS_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.IDENTITY_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.MANAGEMENT_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.NETWORK_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.PROVIDER_ID;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.PROVIDER_TYPE;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.RESOURCE_ID;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ResourceIdPrefixes.HUAWEI_VM;

@Component
public class HuaweiVmWorkflow extends AbstractProviderVmWorkflow {
    @Override
    protected String providerType() {
        return HUAWEI;
    }

    @Override
    public ProviderSession login(ProviderConfiguration providerConfiguration, String userToken) {
        return new ProviderSession(userToken, providerConfiguration.endpointUrl(IAM));
    }

    @Override
    public String createVm(ProviderConfiguration providerConfiguration, ProviderSession session, ResourceRequest request) {
        providerConfiguration.endpointUrl(ECS);
        return HUAWEI_VM + UUID.randomUUID();
    }

    @Override
    public void assignVmAccess(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        providerConfiguration.endpointUrl(VPC);
    }

    @Override
    public void rollbackVm(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        providerConfiguration.endpointUrl(ECS);
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
            IDENTITY_URL, session.endpointUrl(),
            ECS_URL, providerConfiguration.endpointUrl(ECS),
            NETWORK_URL, providerConfiguration.endpointUrl(VPC),
            MANAGEMENT_URL, providerConfiguration.endpointUrl(CONSOLE)
        );
    }
}
