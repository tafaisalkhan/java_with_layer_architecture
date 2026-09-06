package com.mycloud.orchestratorservice.adapter.out.persistence;

import com.mycloud.orchestratorservice.domain.OperationStepName;
import com.mycloud.orchestratorservice.domain.OperationStepStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.Instant;

@Embeddable
public class OperationStepJpaEmbeddable {
    @Enumerated(EnumType.STRING)
    @Column(name = "step_name", nullable = false, length = 64)
    private OperationStepName name;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_status", nullable = false, length = 32)
    private OperationStepStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

    @Column(name = "last_checked_at")
    private Instant lastCheckedAt;
    @Column(name = "provider_status", length = 32)
    private String providerStatus;

    protected OperationStepJpaEmbeddable() {
    }

    public OperationStepJpaEmbeddable(
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
        this.name = name;
        this.status = status;
        this.failureReason = failureReason;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.attemptCount = attemptCount;
        this.nextRetryAt = nextRetryAt;
        this.lastCheckedAt = lastCheckedAt;
        this.providerStatus = providerStatus;
    }

    public OperationStepName getName() {
        return name;
    }

    public OperationStepStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public Instant getNextRetryAt() {
        return nextRetryAt;
    }
    public Instant getLastCheckedAt() { return lastCheckedAt; }
    public String getProviderStatus() { return providerStatus; }
}
