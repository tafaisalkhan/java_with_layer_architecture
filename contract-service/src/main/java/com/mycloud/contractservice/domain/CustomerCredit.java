package com.mycloud.contractservice.domain;

import com.mycloud.common.money.MoneyValue;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CustomerCredit(
    UUID id,
    UUID customerId,
    CreditType type,
    MoneyValue amount,
    MoneyValue remainingAmount,
    String reason,
    Instant createdAt
) {
    public CustomerCredit {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(remainingAmount, "remainingAmount must not be null");
        requireText(reason, "reason");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        if (amount.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("credit amount must be greater than zero");
        }
        if (!amount.currency().equals(remainingAmount.currency())) {
            throw new IllegalArgumentException("credit amount and remaining amount must use the same currency");
        }
    }

    public static CustomerCredit grant(UUID customerId, CreditType type, MoneyValue amount, String reason) {
        return new CustomerCredit(UUID.randomUUID(), customerId, type, amount, amount, reason, Instant.now());
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
