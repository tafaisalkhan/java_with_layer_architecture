package com.mycloud.orchestratorservice.adapter.out.persistence;

import com.mycloud.orchestratorservice.domain.OperationStepName;
import jakarta.persistence.*;

@Entity
@Table(name = "step_config", uniqueConstraints = @UniqueConstraint(columnNames = {"operation_config_id", "step_name"}))
public class StepConfigJpaEntity {
    @Id
    private Long id;
    @Column(name = "operation_config_id", nullable = false)
    private Long operationConfigId;
    @Enumerated(EnumType.STRING)
    @Column(name = "step_name", nullable = false, length = 64)
    private OperationStepName stepName;
    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;
    @Column(name = "retry_enabled", nullable = false)
    private boolean retryEnabled;
    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;
    @Column(name = "retry_delay_seconds", nullable = false)
    private long retryDelaySeconds;
    @Column(name = "required_step", nullable = false)
    private boolean requiredStep;
    @Column(nullable = false)
    private boolean enabled;
    @Column(name = "execution_type", nullable = false, length = 20)
    private String executionType;
    @Column(name = "poll_interval_seconds")
    private Integer pollIntervalSeconds;

    protected StepConfigJpaEntity() {}
    public Long getId() { return id; }
    public OperationStepName getStepName() { return stepName; }
    public int getSequenceNumber() { return sequenceNumber; }
    public boolean isRetryEnabled() { return retryEnabled; }
    public int getMaxAttempts() { return maxAttempts; }
    public long getRetryDelaySeconds() { return retryDelaySeconds; }
    public boolean isRequiredStep() { return requiredStep; }
    public String getExecutionType() { return executionType; }
    public Integer getPollIntervalSeconds() { return pollIntervalSeconds; }
}
