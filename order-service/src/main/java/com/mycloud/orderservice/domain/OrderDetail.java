package com.mycloud.orderservice.domain;

import com.mycloud.common.money.MoneyValue;
import java.math.BigDecimal;
import java.util.Objects;

public record OrderDetail(
    String productName,
    int quantity,
    MoneyValue unitPrice
) {
    public OrderDetail {
        requireText(productName, "productName");
        if (quantity < 1) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        Objects.requireNonNull(unitPrice, "unitPrice must not be null");
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
