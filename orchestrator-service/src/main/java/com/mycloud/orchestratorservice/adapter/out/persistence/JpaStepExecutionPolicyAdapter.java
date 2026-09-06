package com.mycloud.orchestratorservice.adapter.out.persistence;

import com.mycloud.orchestratorservice.adapter.out.persistence.repository.SpringDataStepConfigRepository;
import com.mycloud.orchestratorservice.application.port.out.spi.StepExecutionPolicyPort;
import com.mycloud.orchestratorservice.domain.OperationStepName;
import com.mycloud.orchestratorservice.domain.StepExecutionPolicy;
import java.time.Duration;
import org.springframework.stereotype.Repository;

@Repository
public class JpaStepExecutionPolicyAdapter implements StepExecutionPolicyPort {
    private final SpringDataStepConfigRepository repository;

    public JpaStepExecutionPolicyAdapter(SpringDataStepConfigRepository repository) {
        this.repository = repository;
    }

    @Override
    public StepExecutionPolicy policyFor(OperationStepName stepName) {
        return repository.findFirstByStepNameAndEnabledTrue(stepName)
            .map(entity -> new StepExecutionPolicy(
                entity.getStepName(),
                entity.isRetryEnabled(),
                entity.getMaxAttempts(),
                Duration.ofSeconds(entity.getRetryDelaySeconds()),
                entity.isRequiredStep(),
                false
            ))
            .orElse(new StepExecutionPolicy(stepName, false, 1, Duration.ZERO, true, true));
    }
}
