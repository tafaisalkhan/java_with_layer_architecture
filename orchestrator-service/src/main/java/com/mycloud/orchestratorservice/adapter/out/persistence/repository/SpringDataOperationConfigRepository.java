package com.mycloud.orchestratorservice.adapter.out.persistence.repository;

import com.mycloud.orchestratorservice.adapter.out.persistence.OperationConfigJpaEntity;
import com.mycloud.orchestratorservice.domain.OperationType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOperationConfigRepository extends JpaRepository<OperationConfigJpaEntity, Long> {
    Optional<OperationConfigJpaEntity> findFirstByOperationTypeAndEnabledTrueOrderByVersionDesc(OperationType operationType);
}
