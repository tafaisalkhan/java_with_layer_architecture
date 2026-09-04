package com.mycloud.common.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ContractActivatedEvent(
    UUID contractId,
    UUID customerId,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalAmount,
    String currency,
    List<ContractProductSnapshot> products
) {
}
