package com.mycloud.contractservice.application.port.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ContractProductCommand(
    @NotNull UUID productId,
    @Min(1) int quantity
) {
}
