package com.mycloud.orchestratorservice.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.mycloud.orchestratorservice.domain.OperationPriority;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.UUID;

public record CreateVmCommand(
    @NotNull UUID customerId,
    @NotNull UUID providerId,
    @NotBlank String name,
    @NotBlank String imageId,
    @NotBlank String flavorId,
    @NotBlank String networkId,
    OperationPriority priority,
    @Min(1) @Max(100) Integer quantity
) {
    public OperationPriority requestedPriority() {
        return priority == null ? OperationPriority.NORMAL : priority;
    }

    public int requestedQuantity() {
        return quantity == null ? 1 : quantity;
    }
}
