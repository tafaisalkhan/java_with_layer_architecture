package com.mycloud.orchestratorservice.adapter.out.persistence.repository;

import com.mycloud.orchestratorservice.adapter.out.persistence.OperationStepExecutionConfigJpaEntity;
import com.mycloud.orchestratorservice.domain.OperationStepName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOperationStepExecutionConfigRepository
    extends JpaRepository<OperationStepExecutionConfigJpaEntity, OperationStepName> {
}
