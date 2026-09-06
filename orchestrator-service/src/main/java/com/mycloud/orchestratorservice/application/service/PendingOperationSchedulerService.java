package com.mycloud.orchestratorservice.application.service;

import com.mycloud.orchestratorservice.application.port.out.spi.OperationRepositoryPort;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PendingOperationSchedulerService {
    private final OperationRepositoryPort repository;
    private final OrchestratorApplicationService orchestrator;
    private final int batchSize;
    private final Executor provisioningExecutor;

    public PendingOperationSchedulerService(OperationRepositoryPort repository,
        OrchestratorApplicationService orchestrator,
        @Value("${app.scheduler.pending.batch-size:10}") int batchSize,
        @Qualifier("provisioningExecutor") Executor provisioningExecutor) {
        this.repository = repository;
        this.orchestrator = orchestrator;
        this.batchSize = batchSize;
        this.provisioningExecutor = provisioningExecutor;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.pending.fixed-delay-ms:5000}")
    public void startPendingOperations() {
        repository.claimNextPending(batchSize).forEach(operation ->
            provisioningExecutor.execute(() -> orchestrator.execute(operation)));
    }
}
