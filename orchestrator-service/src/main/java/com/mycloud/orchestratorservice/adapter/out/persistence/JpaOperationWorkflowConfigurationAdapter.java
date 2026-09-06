package com.mycloud.orchestratorservice.adapter.out.persistence;

import com.mycloud.orchestratorservice.adapter.out.persistence.repository.SpringDataOperationConfigRepository;
import com.mycloud.orchestratorservice.adapter.out.persistence.repository.SpringDataStepConfigRepository;
import com.mycloud.orchestratorservice.adapter.out.persistence.repository.SpringDataStepDependencyRepository;
import com.mycloud.orchestratorservice.application.port.out.spi.OperationWorkflowConfigurationPort;
import com.mycloud.orchestratorservice.domain.*;
import java.time.Duration;
import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOperationWorkflowConfigurationAdapter implements OperationWorkflowConfigurationPort {
    private final SpringDataOperationConfigRepository operationConfigs;
    private final SpringDataStepConfigRepository stepConfigs;
    private final SpringDataStepDependencyRepository dependencies;

    public JpaOperationWorkflowConfigurationAdapter(
        SpringDataOperationConfigRepository operationConfigs,
        SpringDataStepConfigRepository stepConfigs,
        SpringDataStepDependencyRepository dependencies
    ) {
        this.operationConfigs = operationConfigs;
        this.stepConfigs = stepConfigs;
        this.dependencies = dependencies;
    }

    @Override
    public OperationWorkflowDefinition activeWorkflowFor(OperationType operationType) {
        var operation = operationConfigs.findFirstByOperationTypeAndEnabledTrueOrderByVersionDesc(operationType)
            .orElseThrow(() -> new IllegalStateException("active operation configuration not found: " + operationType));
        var steps = stepConfigs.findByOperationConfigIdAndEnabledTrueOrderBySequenceNumberAsc(operation.getId());
        Map<Long, StepConfigJpaEntity> byId = new HashMap<>();
        steps.forEach(step -> byId.put(step.getId(), step));
        Map<Long, Set<OperationStepName>> dependenciesByStep = new HashMap<>();
        dependencies.findByStepConfigIdIn(new ArrayList<>(byId.keySet())).forEach(dependency -> {
            var prerequisite = byId.get(dependency.getDependsOnStepConfigId());
            if (prerequisite == null) {
                throw new IllegalStateException("step dependency points outside operation configuration");
            }
            dependenciesByStep.computeIfAbsent(dependency.getStepConfigId(), ignored -> new HashSet<>())
                .add(prerequisite.getStepName());
        });
        var definitions = steps.stream().map(step -> new OperationStepDefinition(
            step.getStepName(),
            step.getSequenceNumber(),
            dependenciesByStep.getOrDefault(step.getId(), Set.of()),
            new StepExecutionPolicy(step.getStepName(), step.isRetryEnabled(), step.getMaxAttempts(),
                Duration.ofSeconds(step.getRetryDelaySeconds()), step.isRequiredStep(), false)
        )).toList();
        return new OperationWorkflowDefinition(operation.getOperationType(), operation.getVersion(),
            operation.getRollbackMode(), operation.isQuotaRollbackEnabled(), definitions);
    }
}
