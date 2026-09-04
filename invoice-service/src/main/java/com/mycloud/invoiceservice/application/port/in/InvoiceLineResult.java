package com.mycloud.invoiceservice.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceLineResult(
    UUID productId,
    String productName,
    int quantity,
    BigDecimal unitAmount,
    String currency,
    BigDecimal lineAmount
) {
}
