package com.example.orderservice.application.port.in;

import java.math.BigDecimal;

public record OrderDetailResult(
    String productName,
    int quantity,
    BigDecimal unitAmount,
    String currency,
    BigDecimal lineAmount
) {
}
