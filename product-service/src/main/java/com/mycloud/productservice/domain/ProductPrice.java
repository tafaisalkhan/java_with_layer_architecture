package com.mycloud.productservice.domain;

import com.mycloud.common.money.MoneyValue;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record ProductPrice(
    UUID id,
    MoneyValue price,
    LocalDate effectiveFrom,
    LocalDate effectiveTo
) {
    public ProductPrice {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(price, "price must not be null");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom must not be null");
        if (effectiveTo != null && effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException("effectiveTo must not be before effectiveFrom");
        }
    }

    public static ProductPrice current(MoneyValue price, LocalDate effectiveFrom) {
        return new ProductPrice(UUID.randomUUID(), price, effectiveFrom, null);
    }

    public ProductPrice close(LocalDate effectiveTo) {
        return new ProductPrice(id, price, effectiveFrom, effectiveTo);
    }

    public boolean isCurrent() {
        return effectiveTo == null;
    }
}
