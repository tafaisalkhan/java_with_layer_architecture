package com.mycloud.contractservice.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CustomerCreditResult(
    UUID creditId,
    UUID customerId,
    String type,
    BigDecimal amount,
    BigDecimal remainingAmount,
    String currency,
    String reason,
    Instant createdAt
) {
}
