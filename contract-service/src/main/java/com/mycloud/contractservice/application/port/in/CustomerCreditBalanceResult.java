package com.mycloud.contractservice.application.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CustomerCreditBalanceResult(
    UUID customerId,
    BigDecimal remainingAmount,
    String currency,
    List<CustomerCreditResult> credits
) {
}
