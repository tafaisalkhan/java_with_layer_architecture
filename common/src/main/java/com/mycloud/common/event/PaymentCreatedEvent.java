package com.mycloud.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreatedEvent(
    UUID paymentId,
    UUID invoiceId,
    BigDecimal amount,
    String currency,
    String status
) {
}
