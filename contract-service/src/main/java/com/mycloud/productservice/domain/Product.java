package com.mycloud.productservice.domain;

import com.mycloud.common.money.MoneyValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Product(
    UUID id,
    String name,
    String description,
    ProductStatus status,
    List<ProductPrice> priceHistory
) {
    public Product {
        Objects.requireNonNull(id, "id must not be null");
        requireText(name, "name");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(priceHistory, "priceHistory must not be null");
        if (priceHistory.isEmpty()) {
            throw new IllegalArgumentException("priceHistory must not be empty");
        }
        priceHistory = List.copyOf(priceHistory);
    }

    public static Product create(String name, String description, MoneyValue price, LocalDate effectiveFrom) {
        return new Product(
            UUID.randomUUID(),
            name,
            description,
            ProductStatus.ACTIVE,
            List.of(ProductPrice.current(price, effectiveFrom))
        );
    }

    public Product updatePrice(MoneyValue newPrice, LocalDate effectiveFrom) {
        ProductPrice latestPrice = priceHistory.stream()
            .max(Comparator.comparing(ProductPrice::effectiveFrom))
            .orElseThrow();
        if (!effectiveFrom.isAfter(latestPrice.effectiveFrom())) {
            throw new IllegalArgumentException("new price effective date must be after the latest price date");
        }

        List<ProductPrice> updatedHistory = new ArrayList<>();
        for (ProductPrice price : priceHistory) {
            updatedHistory.add(price.isCurrent() ? price.close(effectiveFrom) : price);
        }
        updatedHistory.add(ProductPrice.current(newPrice, effectiveFrom));
        return new Product(id, name, description, status, updatedHistory);
    }

    public ProductPrice currentPrice() {
        return priceOn(LocalDate.now());
    }

    public ProductPrice priceOn(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        return priceHistory.stream()
            .filter(price -> !date.isBefore(price.effectiveFrom()))
            .filter(price -> price.effectiveTo() == null || date.isBefore(price.effectiveTo()))
            .max(Comparator.comparing(ProductPrice::effectiveFrom))
            .orElseThrow(() -> new IllegalStateException("no product price is effective on " + date));
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
