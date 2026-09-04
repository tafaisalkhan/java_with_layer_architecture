package com.mycloud.productservice.application.port.in;

import com.mycloud.common.money.MoneyValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateProductCommand(
    @NotBlank String name,
    String description,
    @Valid @NotNull MoneyValue price,
    @NotNull LocalDate effectiveFrom
) {
}
