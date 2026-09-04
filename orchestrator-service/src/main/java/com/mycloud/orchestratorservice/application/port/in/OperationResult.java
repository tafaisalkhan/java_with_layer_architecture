package com.mycloud.orchestratorservice.application.port.in;

import java.util.List;
import java.util.UUID;

public record OperationResult(
    UUID operationId,
    UUID customerId,
    UUID providerId,
    String operationType,
    String resourceType,
    String priority,
    String status,
    String provisionedResourceId,
    String failureReason,
    List<OperationStepResult> steps
) {
}
