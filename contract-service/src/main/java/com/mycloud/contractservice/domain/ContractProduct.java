package com.mycloud.contractservice.domain;

import com.mycloud.common.money.MoneyValue;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record ContractProduct(
    UUID productId,
    String productName,
    int quantity,
    MoneyValue unitPrice,
    int availableQuantity,
    int pendingQuantity,
    int consumedQuantity
) {
    public ContractProduct {
        Objects.requireNonNull(productId, "productId must not be null");
        requireText(productName, "productName");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        Objects.requireNonNull(unitPrice, "unitPrice must not be null");
        if (availableQuantity < 0 || pendingQuantity < 0 || consumedQuantity < 0) {
            throw new IllegalArgumentException("quota quantities must not be negative");
        }
        if (availableQuantity + pendingQuantity + consumedQuantity > quantity) {
            throw new IllegalArgumentException("quota quantities cannot exceed contract quantity");
        }
    }

    public static ContractProduct create(UUID productId, String productName, int quantity, MoneyValue unitPrice) {
        return new ContractProduct(productId, productName, quantity, unitPrice, quantity, 0, 0);
    }

    public ContractProduct reserve(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("reserve amount must be greater than zero");
        }
        if (availableQuantity < amount) {
            throw new IllegalStateException("not enough available quota for product: " + productId);
        }
        return new ContractProduct(
            productId,
            productName,
            quantity,
            unitPrice,
            availableQuantity - amount,
            pendingQuantity + amount,
            consumedQuantity
        );
    }

    public ContractProduct commit(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("commit amount must be greater than zero");
        }
        if (pendingQuantity < amount) {
            throw new IllegalStateException("not enough pending quota for product: " + productId);
        }
        return new ContractProduct(
            productId,
            productName,
            quantity,
            unitPrice,
            availableQuantity,
            pendingQuantity - amount,
            consumedQuantity + amount
        );
    }

    public ContractProduct release(int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("release amount must be greater than zero");
        }
        if (pendingQuantity < amount) {
            throw new IllegalStateException("not enough pending quota for product: " + productId);
        }
        return new ContractProduct(
            productId,
            productName,
            quantity,
            unitPrice,
            availableQuantity + amount,
            pendingQuantity - amount,
            consumedQuantity
        );
    }

    public BigDecimal lineAmount() {
        return unitPrice.amount().multiply(BigDecimal.valueOf(quantity));
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
