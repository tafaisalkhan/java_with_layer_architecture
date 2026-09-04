package com.mycloud.orchestratorservice.adapter.out.persistence;

import com.mycloud.orchestratorservice.domain.OperationStepName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "operation_step_execution_configs")
public class OperationStepExecutionConfigJpaEntity {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "step_name", nullable = false, length = 64)
    private OperationStepName stepName;

    @Column(name = "retry_enabled", nullable = false)
    private boolean retryEnabled;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;

    @Column(name = "retry_delay_seconds", nullable = false)
    private long retryDelaySeconds;

    @Column(name = "required_step", nullable = false)
    private boolean requiredStep;

    @Column(name = "rollback_on_failure", nullable = false)
    private boolean rollbackOnFailure;

    protected OperationStepExecutionConfigJpaEntity() {
    }

    public OperationStepExecutionConfigJpaEntity(
        OperationStepName stepName,
        boolean retryEnabled,
        int maxAttempts,
        long retryDelaySeconds,
        boolean requiredStep,
        boolean rollbackOnFailure
    ) {
        this.stepName = stepName;
        this.retryEnabled = retryEnabled;
        this.maxAttempts = maxAttempts;
        this.retryDelaySeconds = retryDelaySeconds;
        this.requiredStep = requiredStep;
        this.rollbackOnFailure = rollbackOnFailure;
    }

    public OperationStepName getStepName() {
        return stepName;
    }

    public boolean isRetryEnabled() {
        return retryEnabled;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getRetryDelaySeconds() {
        return retryDelaySeconds;
    }

    public boolean isRequiredStep() {
        return requiredStep;
    }

    public boolean isRollbackOnFailure() {
        return rollbackOnFailure;
    }
}
