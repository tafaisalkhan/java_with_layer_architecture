package com.mycloud.providerservice.application.port.in;

import com.mycloud.providerservice.domain.ProviderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateProviderCommand(
    @NotBlank String name,
    @NotNull ProviderType type,
    @NotBlank String adminUsername,
    @NotBlank String credentialSecretRef,
    @Valid @NotEmpty List<ProviderEndpointCommand> endpoints
) {
}
