package com.mycloud.providerservice.application.port.in;

public record ProviderEndpointResult(
    String serviceName,
    String url
) {
}
