package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.domain.Operation;

public interface OperationEventPublisherPort {
    void publishSucceeded(Operation operation);

    void publishFailed(Operation operation);
}
