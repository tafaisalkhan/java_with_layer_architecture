package com.mycloud.contractservice.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record ContractProductResult(
    UUID productId,
    String productName,
    int quantity,
    BigDecimal unitAmount,
    String currency,
    BigDecimal lineAmount,
    int availableQuantity,
    int pendingQuantity,
    int consumedQuantity
) {
}
