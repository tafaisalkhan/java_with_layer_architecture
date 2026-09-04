package com.mycloud.paymentservice.domain;

import com.mycloud.common.money.MoneyValue;
import java.util.Objects;
import java.util.UUID;

public record Payment(
    // Unique ID owned by payment-service. Stored as UUID to avoid cross-service ID collisions.
    UUID id,

    // Invoice ID received from invoice-service.
    UUID invoiceId,

    // Monetary amount for the payment. MoneyValue keeps amount and currency together.
    MoneyValue total,

    // Current business state. Use enum values instead of raw strings.
    PaymentStatus status
) {
    public Payment {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(invoiceId, "invoiceId must not be null");
        Objects.requireNonNull(total, "total must not be null");
        Objects.requireNonNull(status, "status must not be null");
    }

    public static Payment create(UUID invoiceId, MoneyValue total) {
        return new Payment(UUID.randomUUID(), invoiceId, total, PaymentStatus.PENDING);
    }

    public Payment approve() {
        return new Payment(id, invoiceId, total, PaymentStatus.APPROVED);
    }

    public Payment decline() {
        return new Payment(id, invoiceId, total, PaymentStatus.DECLINED);
    }
}
