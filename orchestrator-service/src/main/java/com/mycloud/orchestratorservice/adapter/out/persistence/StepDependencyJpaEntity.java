package com.mycloud.orchestratorservice.adapter.out.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "step_dependency", uniqueConstraints = @UniqueConstraint(columnNames = {"step_config_id", "depends_on_step_config_id"}))
public class StepDependencyJpaEntity {
    @Id
    private Long id;
    @Column(name = "step_config_id", nullable = false)
    private Long stepConfigId;
    @Column(name = "depends_on_step_config_id", nullable = false)
    private Long dependsOnStepConfigId;

    protected StepDependencyJpaEntity() {}
    public Long getStepConfigId() { return stepConfigId; }
    public Long getDependsOnStepConfigId() { return dependsOnStepConfigId; }
}
