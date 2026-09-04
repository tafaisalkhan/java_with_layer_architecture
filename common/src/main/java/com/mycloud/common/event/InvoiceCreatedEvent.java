package com.mycloud.common.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceCreatedEvent(
    UUID invoiceId,
    UUID contractId,
    UUID customerId,
    BigDecimal amount,
    String currency,
    LocalDate issuedDate,
    LocalDate dueDate
) {
}
