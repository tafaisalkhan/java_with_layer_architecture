package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import com.mycloud.orchestratorservice.domain.ResourceRequest;
import java.util.Map;
import java.util.UUID;
import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.mycloud.orchestratorservice.domain.ProvisioningStatus;
import org.springframework.stereotype.Component;

import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderEndpointNames.HORIZON;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderEndpointNames.KEYSTONE;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderEndpointNames.NEUTRON;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderEndpointNames.NOVA;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProviderTypes.OPENSTACK;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.COMPUTE_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.IDENTITY_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.MANAGEMENT_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.NETWORK_URL;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.PROVIDER_ID;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.PROVIDER_TYPE;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ProvisioningMetadataKeys.RESOURCE_ID;
import static com.mycloud.orchestratorservice.adapter.out.provisioning.ResourceIdPrefixes.OPENSTACK_VM;

@Component
public class OpenStackVmWorkflow extends AbstractProviderVmWorkflow {
    private final ConcurrentMap<String, Instant> submittedVms = new ConcurrentHashMap<>();
    public OpenStackVmWorkflow(MockProviderCredentialAuthenticator credentialAuthenticator) {
        super(credentialAuthenticator);
    }
    @Override
    protected String providerType() {
        return OPENSTACK;
    }

    @Override
    public ProviderSession login(ProviderConfiguration providerConfiguration, String userToken) {
        return authenticate(providerConfiguration, userToken, providerConfiguration.endpointUrl(KEYSTONE));
    }

    @Override
    public String createVm(ProviderConfiguration providerConfiguration, ProviderSession session, ResourceRequest request) {
        providerConfiguration.endpointUrl(NOVA);
        String resourceId = OPENSTACK_VM + UUID.randomUUID();
        submittedVms.put(resourceId, Instant.now());
        return resourceId;
    }

    @Override
    public ProvisioningStatus getVmStatus(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        providerConfiguration.endpointUrl(NOVA);
        Instant submittedAt = submittedVms.get(resourceId);
        return submittedAt != null && Duration.between(submittedAt, Instant.now()).toSeconds() < 10
            ? ProvisioningStatus.BUILDING : ProvisioningStatus.ACTIVE;
    }

    @Override
    public void assignVmAccess(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        providerConfiguration.endpointUrl(NEUTRON);
    }

    @Override
    public void rollbackVm(ProviderConfiguration providerConfiguration, ProviderSession session, String resourceId) {
        providerConfiguration.endpointUrl(NOVA);
        submittedVms.remove(resourceId);
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
            COMPUTE_URL, providerConfiguration.endpointUrl(NOVA),
            NETWORK_URL, providerConfiguration.endpointUrl(NEUTRON),
            MANAGEMENT_URL, providerConfiguration.endpointUrl(HORIZON)
        );
    }
}
