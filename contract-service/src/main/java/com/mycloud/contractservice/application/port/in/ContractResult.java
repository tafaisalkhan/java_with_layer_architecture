package com.mycloud.contractservice.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ContractResult(
    UUID contractId,
    UUID customerId,
    String type,
    LocalDate startDate,
    LocalDate endDate,
    String status,
    BigDecimal totalAmount,
    String currency,
    List<ContractProductResult> products
) {
}
