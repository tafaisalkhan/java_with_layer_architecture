package com.mycloud.productservice.application.port.in;

import com.mycloud.common.money.MoneyValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateProductPriceCommand(
    @NotNull UUID productId,
    @Valid @NotNull MoneyValue price,
    @NotNull LocalDate effectiveFrom
) {
}
