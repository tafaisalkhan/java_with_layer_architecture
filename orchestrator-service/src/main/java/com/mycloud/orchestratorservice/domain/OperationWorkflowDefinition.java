package com.mycloud.orchestratorservice.domain;

import java.util.Comparator;
import java.util.List;

public record OperationWorkflowDefinition(
    OperationType operationType,
    int version,
    RollbackMode rollbackMode,
    boolean quotaRollbackEnabled,
    List<OperationStepDefinition> steps
) {
    public OperationWorkflowDefinition {
        steps = steps.stream()
            .sorted(Comparator.comparingInt(OperationStepDefinition::sequenceNumber))
            .toList();
    }
}
