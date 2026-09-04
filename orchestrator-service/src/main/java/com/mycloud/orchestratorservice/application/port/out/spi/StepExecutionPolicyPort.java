package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.domain.OperationStepName;
import com.mycloud.orchestratorservice.domain.StepExecutionPolicy;

public interface StepExecutionPolicyPort {
    StepExecutionPolicy policyFor(OperationStepName stepName);
}
