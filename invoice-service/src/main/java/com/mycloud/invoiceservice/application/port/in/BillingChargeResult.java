package com.mycloud.invoiceservice.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BillingChargeResult(
    UUID chargeId,
    UUID operationId,
    UUID customerId,
    String resourceId,
    String resourceType,
    BigDecimal amount,
    String currency,
    String status,
    Instant createdAt
) {
}
