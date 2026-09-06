package com.mycloud.orchestratorservice.adapter.out.persistence;

import com.mycloud.orchestratorservice.domain.OperationType;
import com.mycloud.orchestratorservice.domain.RollbackMode;
import jakarta.persistence.*;

@Entity
@Table(name = "operation_config", uniqueConstraints = @UniqueConstraint(columnNames = {"operation_type", "version"}))
public class OperationConfigJpaEntity {
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 64)
    private OperationType operationType;
    @Column(nullable = false)
    private int version;
    @Column(nullable = false)
    private boolean enabled;
    @Enumerated(EnumType.STRING)
    @Column(name = "rollback_mode", nullable = false, length = 16)
    private RollbackMode rollbackMode;
    @Column(name = "quota_rollback_enabled", nullable = false)
    private boolean quotaRollbackEnabled;

    protected OperationConfigJpaEntity() {}
    public Long getId() { return id; }
    public OperationType getOperationType() { return operationType; }
    public int getVersion() { return version; }
    public RollbackMode getRollbackMode() { return rollbackMode; }
    public boolean isQuotaRollbackEnabled() { return quotaRollbackEnabled; }
}
