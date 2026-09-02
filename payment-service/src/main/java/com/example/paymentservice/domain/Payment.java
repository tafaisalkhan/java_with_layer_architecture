package com.example.paymentservice.domain;

import com.example.common.money.MoneyValue;
import java.util.Objects;
import java.util.UUID;

public record Payment(
    // Unique ID owned by payment-service. Stored as UUID to avoid cross-service ID collisions.
    UUID id,

    // Order ID received from order-service.
    UUID orderId,

    // Monetary amount for the payment. MoneyValue keeps amount and currency together.
    MoneyValue total,

    // Current business state. Use enum values instead of raw strings.
    PaymentStatus status
) {
    public Payment {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(total, "total must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    public static Payment create(UUID orderId, MoneyValue total) {
        return new Payment(UUID.randomUUID(), orderId, total, PaymentStatus.PENDING);
    }

    public Payment approve() {
        return new Payment(id, orderId, total, PaymentStatus.APPROVED);
    }

    public Payment decline() {
        return new Payment(id, orderId, total, PaymentStatus.DECLINED);
    }
}
