package com.mycloud.productservice.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductQuoteResult(
    UUID productId,
    String productName,
    BigDecimal amount,
    String currency
) {
}
