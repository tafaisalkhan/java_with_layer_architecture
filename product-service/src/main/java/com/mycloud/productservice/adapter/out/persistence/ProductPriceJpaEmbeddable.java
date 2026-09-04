package com.mycloud.productservice.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Embeddable
public class ProductPriceJpaEmbeddable {
    @Column(name = "price_id", nullable = false)
    private UUID priceId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    protected ProductPriceJpaEmbeddable() {
    }

    public ProductPriceJpaEmbeddable(
        UUID priceId,
        BigDecimal amount,
        String currency,
        LocalDate effectiveFrom,
        LocalDate effectiveTo
    ) {
        this.priceId = priceId;
        this.amount = amount;
        this.currency = currency;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    public UUID getPriceId() {
        return priceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }
}
