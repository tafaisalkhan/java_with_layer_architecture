package com.mycloud.providerservice.application.port.in;

import jakarta.validation.constraints.NotBlank;

public record ProviderEndpointCommand(
    @NotBlank String serviceName,
    @NotBlank String url
) {
}
