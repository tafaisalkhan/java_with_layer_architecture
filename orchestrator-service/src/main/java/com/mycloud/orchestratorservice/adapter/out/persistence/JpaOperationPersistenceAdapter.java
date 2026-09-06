package com.mycloud.orchestratorservice.adapter.out.persistence;

import com.mycloud.orchestratorservice.adapter.out.persistence.repository.SpringDataOperationRepository;
import com.mycloud.orchestratorservice.application.port.out.spi.OperationRepositoryPort;
import com.mycloud.orchestratorservice.domain.Operation;
import com.mycloud.orchestratorservice.domain.OperationStep;
import com.mycloud.orchestratorservice.domain.ResourceRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JpaOperationPersistenceAdapter implements OperationRepositoryPort {
    private final SpringDataOperationRepository springDataOperationRepository;

    public JpaOperationPersistenceAdapter(SpringDataOperationRepository springDataOperationRepository) {
        this.springDataOperationRepository = springDataOperationRepository;
    }

    @Override
    public Operation save(Operation operation) {
        return toDomain(springDataOperationRepository.save(toEntity(operation)));
    }

    @Override
    public Optional<Operation> findById(UUID operationId) {
        return springDataOperationRepository.findById(operationId).map(this::toDomain);
    }

    private OperationJpaEntity toEntity(Operation operation) {
        return new OperationJpaEntity(
            operation.id(),
            operation.customerId(),
            operation.providerId(),
            operation.type(),
            operation.resourceType(),
            operation.priority(),
            operation.resourceRequest().name(),
            operation.resourceRequest().imageId(),
            operation.resourceRequest().flavorId(),
            operation.resourceRequest().networkId(),
            operation.status(),
            operation.provisionedResourceId(),
            operation.failureReason(),
            operation.steps().stream().map(this::toStepEntity).toList()
        );
    }

    private Operation toDomain(OperationJpaEntity entity) {
        return new Operation(
            entity.getId(),
            entity.getCustomerId(),
            entity.getProviderId(),
            entity.getType(),
            entity.getResourceType(),
            entity.getPriority(),
            new ResourceRequest(entity.getResourceName(), entity.getImageId(), entity.getFlavorId(), entity.getNetworkId()),
            entity.getStatus(),
            entity.getProvisionedResourceId(),
            entity.getFailureReason(),
            entity.getSteps().stream().map(this::toStepDomain).toList()
        );
    }

    @Override
    @Transactional
    public List<Operation> claimNextRunnable(int limit) {
        List<OperationJpaEntity> claimed = springDataOperationRepository.findNextRunnableForUpdate(
            org.springframework.data.domain.PageRequest.of(0, limit)
        );
        claimed.forEach(OperationJpaEntity::markRunning);
        springDataOperationRepository.flush();
        return claimed.stream().map(this::toDomain).toList();
    }

    private OperationStepJpaEmbeddable toStepEntity(OperationStep step) {
        return new OperationStepJpaEmbeddable(
            step.name(),
            step.status(),
            step.failureReason(),
            step.startedAt(),
            step.finishedAt(),
            step.attemptCount(),
            step.nextRetryAt(),
            step.lastCheckedAt(),
            step.providerStatus()
        );
    }

    private OperationStep toStepDomain(OperationStepJpaEmbeddable entity) {
        return new OperationStep(
            entity.getName(),
            entity.getStatus(),
            entity.getFailureReason(),
            entity.getStartedAt(),
            entity.getFinishedAt(),
            entity.getAttemptCount(),
            entity.getNextRetryAt(),
            entity.getLastCheckedAt(),
            entity.getProviderStatus()
        );
    }
}
