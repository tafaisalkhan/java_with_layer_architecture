package com.mycloud.orchestratorservice.application.port.out.spi.dto;

public record ProviderEndpointView(
    String serviceName,
    String url
) {
}
