package com.mycloud.invoiceservice.adapter.out.persistence;

import com.mycloud.invoiceservice.domain.BillingChargeStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "billing_charges")
public class BillingChargeJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "operation_id", nullable = false, unique = true)
    private UUID operationId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "resource_id", nullable = false)
    private String resourceId;

    @Column(name = "resource_type", nullable = false, length = 32)
    private String resourceType;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private BillingChargeStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected BillingChargeJpaEntity() {
    }

    public BillingChargeJpaEntity(
        UUID id,
        UUID operationId,
        UUID customerId,
        String resourceId,
        String resourceType,
        BigDecimal amount,
        String currency,
        BillingChargeStatus status,
        Instant createdAt
    ) {
        this.id = id;
        this.operationId = operationId;
        this.customerId = customerId;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOperationId() {
        return operationId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public BillingChargeStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
