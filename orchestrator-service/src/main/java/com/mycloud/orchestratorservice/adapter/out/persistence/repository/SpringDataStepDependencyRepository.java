package com.mycloud.orchestratorservice.adapter.out.persistence.repository;

import com.mycloud.orchestratorservice.adapter.out.persistence.StepDependencyJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataStepDependencyRepository extends JpaRepository<StepDependencyJpaEntity, Long> {
    List<StepDependencyJpaEntity> findByStepConfigIdIn(List<Long> stepConfigIds);
}
