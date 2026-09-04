package com.mycloud.productservice.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProductPriceResult(
    UUID priceId,
    BigDecimal amount,
    String currency,
    LocalDate effectiveFrom,
    LocalDate effectiveTo
) {
}
