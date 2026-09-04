package com.mycloud.common.event;

import java.time.Instant;
import java.util.UUID;

public record OperationSucceededEvent(
    UUID operationId,
    UUID customerId,
    UUID providerId,
    String operationType,
    String resourceType,
    String resourceId,
    Instant occurredAt
) {
}
