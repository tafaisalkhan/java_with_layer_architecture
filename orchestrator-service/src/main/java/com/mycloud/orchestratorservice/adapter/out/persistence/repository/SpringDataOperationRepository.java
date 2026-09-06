package com.mycloud.orchestratorservice.adapter.out.persistence.repository;

import com.mycloud.orchestratorservice.adapter.out.persistence.OperationJpaEntity;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface SpringDataOperationRepository extends JpaRepository<OperationJpaEntity, UUID> {
    @Override
    @EntityGraph(attributePaths = "steps")
    java.util.Optional<OperationJpaEntity> findById(UUID operationId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select operation from OperationJpaEntity operation
        where operation.status in (
            com.mycloud.orchestratorservice.domain.OperationStatus.PENDING,
            com.mycloud.orchestratorservice.domain.OperationStatus.WAITING
        )
        and not exists (
            select retryStep from operation.steps retryStep
            where retryStep.nextRetryAt is not null
            and retryStep.nextRetryAt > current_timestamp
        )
        order by case operation.priority
            when com.mycloud.orchestratorservice.domain.OperationPriority.CRITICAL then 0
            when com.mycloud.orchestratorservice.domain.OperationPriority.HIGH then 1
            when com.mycloud.orchestratorservice.domain.OperationPriority.NORMAL then 2
            when com.mycloud.orchestratorservice.domain.OperationPriority.LOW then 3
            else 4
        end
        """)
    java.util.List<OperationJpaEntity> findNextRunnableForUpdate(Pageable pageable);
}
