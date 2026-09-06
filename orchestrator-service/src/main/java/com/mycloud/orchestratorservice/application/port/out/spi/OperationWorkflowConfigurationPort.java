package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.domain.OperationType;
import com.mycloud.orchestratorservice.domain.OperationWorkflowDefinition;

public interface OperationWorkflowConfigurationPort {
    OperationWorkflowDefinition activeWorkflowFor(OperationType operationType);
}
