package com.mycloud.contractservice.application.port.out.spi.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductQuote(
    UUID productId,
    String productName,
    BigDecimal amount,
    String currency
) {
}
