package com.mycloud.orchestratorservice.application.service;

import com.mycloud.orchestratorservice.application.port.out.spi.OperationRepositoryPort;
import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class WaitingOperationPollingSchedulerService {
    private final OperationRepositoryPort repository;
    private final OrchestratorApplicationService orchestrator;
    private final int batchSize;
    private final Executor pollingExecutor;

    public WaitingOperationPollingSchedulerService(OperationRepositoryPort repository,
        OrchestratorApplicationService orchestrator,
        @Value("${app.scheduler.polling.batch-size:50}") int batchSize,
        @Qualifier("pollingExecutor") Executor pollingExecutor) {
        this.repository = repository;
        this.orchestrator = orchestrator;
        this.batchSize = batchSize;
        this.pollingExecutor = pollingExecutor;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.polling.fixed-delay-ms:2000}")
    public void resumeWaitingOperations() {
        repository.claimWaitingReadyForPoll(batchSize).forEach(operation ->
            pollingExecutor.execute(() -> orchestrator.execute(operation)));
    }
}
