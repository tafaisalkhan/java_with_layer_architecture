package com.mycloud.invoiceservice.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record InvoiceResult(
    UUID invoiceId,
    UUID contractId,
    UUID customerId,
    LocalDate issuedDate,
    LocalDate dueDate,
    String status,
    BigDecimal amount,
    String currency,
    List<InvoiceLineResult> lines
) {
}
