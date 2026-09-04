package com.mycloud.orchestratorservice.domain;

import java.time.Instant;
import java.util.Objects;

public record OperationStep(
    OperationStepName name,
    OperationStepStatus status,
    String failureReason,
    Instant startedAt,
    Instant finishedAt,
    int attemptCount,
    Instant nextRetryAt
) {
    public OperationStep {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    public static OperationStep pending(OperationStepName name) {
        return new OperationStep(name, OperationStepStatus.PENDING, null, null, null, 0, null);
    }

    public OperationStep start(Instant now) {
        return new OperationStep(name, OperationStepStatus.RUNNING, null, now, null, attemptCount + 1, null);
    }

    public OperationStep succeed(Instant now) {
        return new OperationStep(name, OperationStepStatus.SUCCEEDED, null, startedAt, now, attemptCount, null);
    }

    public OperationStep fail(String reason, Instant now) {
        return new OperationStep(name, OperationStepStatus.FAILED, reason, startedAt, now, attemptCount, null);
    }

    public OperationStep retryLater(String reason, Instant now, Instant nextRetryAt) {
        return new OperationStep(name, OperationStepStatus.PENDING, reason, startedAt, now, attemptCount, nextRetryAt);
    }
}
