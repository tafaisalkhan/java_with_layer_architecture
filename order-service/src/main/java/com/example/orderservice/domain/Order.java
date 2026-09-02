package com.example.orderservice.domain;

import com.example.common.money.MoneyValue;
import java.util.Objects;
import java.util.UUID;

public record Order(
    // Unique ID owned by order-service. Stored as UUID to avoid cross-service ID collisions.
    UUID id,

    // Customer ID received from customer-service. This service validates it through an output port.
    UUID customerId,

    // Payment ID returned by payment-service after payment creation.
    UUID paymentId,

    // Monetary amount for the order. MoneyValue keeps amount and currency together.
    MoneyValue total,

    // Current business state. Use enum values instead of raw strings.
    OrderStatus status
) {
    public Order {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(total, "total must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    public static Order create(UUID customerId, MoneyValue total) {
        return new Order(UUID.randomUUID(), customerId, null, total, OrderStatus.CREATED);
    }

    public Order markPaymentPending(UUID paymentId) {
        Objects.requireNonNull(paymentId, "paymentId must not be null");
        return new Order(id, customerId, paymentId, total, OrderStatus.PAYMENT_PENDING);
    }

    public Order markPaid() {
        if (paymentId == null) {
            throw new IllegalStateException("order must have a payment before it can be paid");
        }
        return new Order(id, customerId, paymentId, total, OrderStatus.PAID);
    }

    public Order cancel() {
        return new Order(id, customerId, paymentId, total, OrderStatus.CANCELLED);
    }
}
