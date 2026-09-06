package com.mycloud.orchestratorservice.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.mycloud.orchestratorservice.domain.OperationPriority;
import java.util.UUID;

public record CreateVmCommand(
    @NotNull UUID customerId,
    @NotNull UUID providerId,
    @NotNull UUID contractId,
    @NotNull com.mycloud.orchestratorservice.domain.OperationType operationType,
    @NotNull com.mycloud.orchestratorservice.domain.ResourceType resourceType,
    @NotBlank String name,
    @NotBlank String imageId,
    @NotBlank String flavorId,
    @NotBlank String networkId,
    OperationPriority priority
) {
    public OperationPriority requestedPriority() {
        return priority == null ? OperationPriority.NORMAL : priority;
    }
}
