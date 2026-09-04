package com.mycloud.orchestratorservice.application.service;

import com.mycloud.orchestratorservice.application.port.out.spi.OperationRepositoryPort;
import com.mycloud.orchestratorservice.domain.Operation;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OperationSchedulerService {
    private final OperationRepositoryPort operationRepositoryPort;
    private final OrchestratorApplicationService orchestratorApplicationService;
    private final int batchSize;

    public OperationSchedulerService(
        OperationRepositoryPort operationRepositoryPort,
        OrchestratorApplicationService orchestratorApplicationService,
        @Value("${app.scheduler.batch-size:5}") int batchSize
    ) {
        this.operationRepositoryPort = operationRepositoryPort;
        this.orchestratorApplicationService = orchestratorApplicationService;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.fixed-delay-ms:5000}")
    public void runPendingOperations() {
        List<Operation> operations = operationRepositoryPort.findNextPending(batchSize);
        operations.forEach(orchestratorApplicationService::execute);
    }
}
