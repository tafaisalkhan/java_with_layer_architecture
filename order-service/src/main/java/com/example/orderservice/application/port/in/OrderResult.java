package com.example.orderservice.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderResult(
    // Unique order ID owned by order-service.
    UUID orderId,

    // Customer ID from customer-service.
    UUID customerId,

    // Payment ID returned by payment-service after payment creation.
    UUID paymentId,

    // Exact total order amount.
    BigDecimal amount,

    // ISO 4217 currency code, for example PKR.
    String currency,

    // Current order state. Enum prevents invalid free-text status values.
    OrderStatusView status
) {
}
