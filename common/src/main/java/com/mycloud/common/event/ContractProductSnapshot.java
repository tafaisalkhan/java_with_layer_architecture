package com.mycloud.common.event;

import java.math.BigDecimal;
import java.util.UUID;

public record ContractProductSnapshot(
    UUID productId,
    String productName,
    int quantity,
    BigDecimal unitAmount,
    String currency,
    BigDecimal lineAmount
) {
}
