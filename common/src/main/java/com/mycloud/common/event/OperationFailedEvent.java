package com.mycloud.common.event;

import java.time.Instant;
import java.util.UUID;

public record OperationFailedEvent(
    UUID operationId,
    UUID customerId,
    String operationType,
    String resourceType,
    String reason,
    Instant occurredAt
) {
}
