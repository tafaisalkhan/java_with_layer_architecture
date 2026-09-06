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
    Instant nextRetryAt,
    Instant lastCheckedAt,
    String providerStatus
) {
    public OperationStep {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    public static OperationStep pending(OperationStepName name) {
        return new OperationStep(name, OperationStepStatus.PENDING, null, null, null, 0, null, null, null);
    }

    public OperationStep start(Instant now) {
        return new OperationStep(name, OperationStepStatus.RUNNING, null, startedAt == null ? now : startedAt, null, attemptCount + 1, null, lastCheckedAt, providerStatus);
    }

    public OperationStep succeed(Instant now) {
        return new OperationStep(name, OperationStepStatus.SUCCEEDED, null, startedAt, now, attemptCount, null, now, providerStatus);
    }

    public OperationStep succeed(Instant now, String terminalProviderStatus) {
        return new OperationStep(name, OperationStepStatus.SUCCEEDED, null, startedAt, now, attemptCount, null, now, terminalProviderStatus);
    }

    public OperationStep fail(String reason, Instant now) {
        return new OperationStep(name, OperationStepStatus.FAILED, reason, startedAt, now, attemptCount, null, now, providerStatus);
    }

    public OperationStep retryLater(String reason, Instant now, Instant nextRetryAt) {
        return new OperationStep(name, OperationStepStatus.PENDING, reason, startedAt, now, attemptCount, nextRetryAt, now, providerStatus);
    }

    public OperationStep waitForPoll(Instant now, Instant nextPollAt, String status) {
        return new OperationStep(name, OperationStepStatus.WAITING, null, startedAt == null ? now : startedAt,
            null, attemptCount, nextPollAt, now, status);
    }
}
