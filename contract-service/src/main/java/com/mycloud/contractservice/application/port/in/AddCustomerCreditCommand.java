package com.mycloud.contractservice.application.port.in;

import com.mycloud.contractservice.domain.CreditType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record AddCustomerCreditCommand(
    @NotNull UUID customerId,
    CreditType type,
    @NotNull @DecimalMin("0.01") BigDecimal amount,
    @NotBlank String currency,
    @NotBlank String reason
) {
    public CreditType requestedType() {
        return type == null ? CreditType.ADJUSTMENT : type;
    }
}
