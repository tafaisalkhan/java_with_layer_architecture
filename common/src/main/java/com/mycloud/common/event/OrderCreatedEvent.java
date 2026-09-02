package com.mycloud.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
    UUID orderId,
    UUID customerId,
    BigDecimal amount,
    String currency
) {
}
