package com.mycloud.orderservice.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class OrderDetailJpaEmbeddable {
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    protected OrderDetailJpaEmbeddable() {
    }

    public OrderDetailJpaEmbeddable(String productName, int quantity, BigDecimal unitAmount, String currency) {
        this.productName = productName;
        this.quantity = quantity;
        this.unitAmount = unitAmount;
        this.currency = currency;
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
}
