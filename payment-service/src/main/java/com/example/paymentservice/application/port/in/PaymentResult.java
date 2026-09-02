package com.example.paymentservice.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResult(
    // Unique payment ID owned by payment-service.
    UUID paymentId,

    // Order ID from order-service.
    UUID orderId,

    // Exact payment amount.
    BigDecimal amount,

    // ISO 4217 currency code, for example PKR.
    String currency,

    // Current payment state. Enum prevents invalid free-text status values.
    PaymentStatusView status
) {
}
