package com.mycloud.contractservice.adapter.out.persistence;

import com.mycloud.contractservice.domain.CreditType;
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
@Table(name = "customer_credits")
public class CustomerCreditJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_type", nullable = false, length = 32)
    private CreditType type;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "remaining_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected CustomerCreditJpaEntity() {
    }

    public CustomerCreditJpaEntity(
        UUID id,
        UUID customerId,
        CreditType type,
        BigDecimal amount,
        BigDecimal remainingAmount,
        String currency,
        String reason,
        Instant createdAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.amount = amount;
        this.remainingAmount = remainingAmount;
        this.currency = currency;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public CreditType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
