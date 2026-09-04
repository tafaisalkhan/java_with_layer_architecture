package com.mycloud.orchestratorservice.application.port.out.spi.dto;

import java.util.Map;

public record ProvisionedResource(
    String resourceId,
    String providerType,
    String managementUrl,
    Map<String, String> attributes
) {
    public ProvisionedResource {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
