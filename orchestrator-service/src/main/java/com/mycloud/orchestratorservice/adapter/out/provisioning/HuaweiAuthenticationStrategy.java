package com.mycloud.orchestratorservice.adapter.out.provisioning;

import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import org.springframework.stereotype.Component;

@Component
public class HuaweiAuthenticationStrategy implements ProviderAuthenticationStrategy {
    private final HuaweiProjectTokenService projectTokenService;

    public HuaweiAuthenticationStrategy(HuaweiProjectTokenService projectTokenService) {
        this.projectTokenService = projectTokenService;
    }

    @Override
    public String providerType() {
        return ProviderTypes.HUAWEI;
    }

    @Override
    public ProviderSession authenticate(ProviderConfiguration provider, String userToken, String endpointUrl) {
        return projectTokenService.sessionFor(provider, endpointUrl);
    }
}
