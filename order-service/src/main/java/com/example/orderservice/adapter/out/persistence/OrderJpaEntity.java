package com.example.orderservice.adapter.out.persistence;

import com.example.orderservice.domain.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OrderStatus status;

    @ElementCollection
    @CollectionTable(name = "order_details", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderDetailJpaEmbeddable> details = new ArrayList<>();

    protected OrderJpaEntity() {
    }

    public OrderJpaEntity(
        UUID id,
        UUID customerId,
        UUID paymentId,
        BigDecimal amount,
        String currency,
        OrderStatus status,
        List<OrderDetailJpaEmbeddable> details
    ) {
        this.id = id;
        this.customerId = customerId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.details = new ArrayList<>(details);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderDetailJpaEmbeddable> getDetails() {
        return details;
    }
}
