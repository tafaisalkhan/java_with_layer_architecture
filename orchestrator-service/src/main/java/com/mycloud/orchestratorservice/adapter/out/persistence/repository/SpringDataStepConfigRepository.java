package com.mycloud.orchestratorservice.adapter.out.persistence.repository;

import com.mycloud.orchestratorservice.adapter.out.persistence.StepConfigJpaEntity;
import com.mycloud.orchestratorservice.domain.OperationStepName;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataStepConfigRepository extends JpaRepository<StepConfigJpaEntity, Long> {
    List<StepConfigJpaEntity> findByOperationConfigIdAndEnabledTrueOrderBySequenceNumberAsc(Long operationConfigId);
    Optional<StepConfigJpaEntity> findFirstByStepNameAndEnabledTrue(OperationStepName stepName);
}
