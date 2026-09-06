package com.mycloud.orchestratorservice.domain;

import java.util.Set;

public record OperationStepDefinition(
    OperationStepName stepName,
    int sequenceNumber,
    Set<OperationStepName> dependencies,
    StepExecutionPolicy executionPolicy
) {
    public OperationStepDefinition {
        dependencies = Set.copyOf(dependencies);
    }
}
