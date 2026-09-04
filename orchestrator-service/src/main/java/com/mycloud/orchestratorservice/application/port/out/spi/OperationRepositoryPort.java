package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.domain.Operation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationRepositoryPort {
    Operation save(Operation operation);

    Optional<Operation> findById(UUID operationId);

    List<Operation> findNextPending(int limit);
}
