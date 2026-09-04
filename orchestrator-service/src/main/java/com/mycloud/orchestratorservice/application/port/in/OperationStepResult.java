package com.mycloud.orchestratorservice.application.port.in;

import java.time.Instant;

public record OperationStepResult(
    String name,
    String status,
    String failureReason,
    Instant startedAt,
    Instant finishedAt,
    int attemptCount,
    Instant nextRetryAt
) {
}
