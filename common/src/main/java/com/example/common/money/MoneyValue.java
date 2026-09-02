package com.example.common.money;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record MoneyValue(
    // Exact money amount. BigDecimal avoids rounding errors from double or float.
    @NotNull @DecimalMin("0.01") BigDecimal amount,

    // ISO 4217 currency code, for example PKR, USD, or EUR.
    @NotBlank String currency
) {
}
