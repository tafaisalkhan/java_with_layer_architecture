package com.mycloud.orchestratorservice.application.port.out.spi.dto;

public record ProviderSession(
    String accessToken,
    String endpointUrl
) {
}
