package com.mycloud.contractservice.domain;

import com.mycloud.common.money.MoneyValue;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Contract(
    UUID id,
    UUID customerId,
    ContractType type,
    LocalDate startDate,
    LocalDate endDate,
    ContractStatus status,
    List<ContractProduct> products
) {
    public Contract {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(startDate, "startDate must not be null");
        Objects.requireNonNull(endDate, "endDate must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(products, "products must not be null");
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        validateContractDuration(type, startDate, endDate);
        if (products.isEmpty()) {
            throw new IllegalArgumentException("contract must contain at least one product");
        }
        products = List.copyOf(products);
        ensureSingleCurrency(products);
    }

    public static Contract create(UUID customerId, ContractType type, LocalDate startDate, LocalDate endDate, List<ContractProduct> products) {
        return new Contract(UUID.randomUUID(), customerId, type, startDate, endDate, ContractStatus.ACTIVE, products);
    }

    public MoneyValue total() {
        String currency = products.get(0).unitPrice().currency();
        BigDecimal amount = products.stream()
            .map(ContractProduct::lineAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new MoneyValue(amount, currency);
    }

    public Contract cancel() {
        return new Contract(id, customerId, type, startDate, endDate, ContractStatus.CANCELLED, products);
    }

    public Contract reserveQuota(UUID productId, int amount) {
        return updateProduct(productId, product -> product.reserve(amount));
    }

    public Contract commitQuota(UUID productId, int amount) {
        return updateProduct(productId, product -> product.commit(amount));
    }

    public Contract releaseQuota(UUID productId, int amount) {
        return updateProduct(productId, product -> product.release(amount));
    }

    private Contract updateProduct(UUID productId, java.util.function.Function<ContractProduct, ContractProduct> update) {
        boolean exists = products.stream().anyMatch(product -> product.productId().equals(productId));
        if (!exists) {
            throw new IllegalStateException("contract product not found: " + productId);
        }
        return new Contract(
            id,
            customerId,
            type,
            startDate,
            endDate,
            status,
            products.stream()
                .map(product -> product.productId().equals(productId) ? update.apply(product) : product)
                .toList()
        );
    }

    private static void ensureSingleCurrency(List<ContractProduct> products) {
        String currency = products.get(0).unitPrice().currency();
        boolean hasDifferentCurrency = products.stream()
            .anyMatch(product -> !product.unitPrice().currency().equals(currency));
        if (hasDifferentCurrency) {
            throw new IllegalArgumentException("all contract products must use the same currency");
        }
    }

    private static void validateContractDuration(ContractType type, LocalDate startDate, LocalDate endDate) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        if (type == ContractType.PAY_AS_YOU_GO && (days < 1 || days > 365)) {
            throw new IllegalArgumentException("pay-as-you-go contract duration must be between 1 and 365 days");
        }
        if ((type == ContractType.FIXED_CONTRACT || type == ContractType.RESERVED) && days < 30) {
            throw new IllegalArgumentException("fixed and reserved contracts must be at least 30 days");
        }
    }
}
