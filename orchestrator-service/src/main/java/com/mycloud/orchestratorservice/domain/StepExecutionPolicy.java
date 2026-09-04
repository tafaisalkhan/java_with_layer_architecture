package com.mycloud.orchestratorservice.domain;

import java.time.Duration;

public record StepExecutionPolicy(
    OperationStepName stepName,
    boolean retryEnabled,
    int maxAttempts,
    Duration retryDelay,
    boolean requiredStep,
    boolean rollbackOnFailure
) {
}
