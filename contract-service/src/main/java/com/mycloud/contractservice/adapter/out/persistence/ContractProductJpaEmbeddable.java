package com.mycloud.contractservice.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
public class ContractProductJpaEmbeddable {
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "pending_quantity", nullable = false)
    private int pendingQuantity;

    @Column(name = "consumed_quantity", nullable = false)
    private int consumedQuantity;

    protected ContractProductJpaEmbeddable() {
    }

    public ContractProductJpaEmbeddable(
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitAmount,
        String currency,
        int availableQuantity,
        int pendingQuantity,
        int consumedQuantity
    ) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitAmount = unitAmount;
        this.currency = currency;
        this.availableQuantity = availableQuantity;
        this.pendingQuantity = pendingQuantity;
        this.consumedQuantity = consumedQuantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitAmount() {
        return unitAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public int getPendingQuantity() {
        return pendingQuantity;
    }

    public int getConsumedQuantity() {
        return consumedQuantity;
    }
}
