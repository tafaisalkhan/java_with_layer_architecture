package com.mycloud.providerservice.application.port.in;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AllowCustomerProviderCommand(
    @NotNull UUID providerId,
    @NotNull UUID customerId
) {
}
