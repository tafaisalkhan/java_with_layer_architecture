package com.example.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreatedEvent(
    UUID paymentId,
    UUID orderId,
    BigDecimal amount,
    String currency,
    String status
) {
}
