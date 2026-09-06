package com.mycloud.orchestratorservice.application.port.out.spi;

import com.mycloud.orchestratorservice.domain.Operation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationRepositoryPort {
    Operation save(Operation operation);

    Optional<Operation> findById(UUID operationId);

    /**
     * Atomically locks and transitions the next eligible operations to RUNNING.
     * This is the scheduler's ownership boundary and prevents two workers or
     * service instances from executing the same operation concurrently.
     */
    List<Operation> claimNextPending(int limit);

    List<Operation> claimWaitingReadyForPoll(int limit);
}
