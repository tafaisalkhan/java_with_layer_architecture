package com.mycloud.productservice.adapter.in.web;

import com.mycloud.common.money.MoneyValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdateProductPriceRequest(
    @Valid @NotNull MoneyValue price,
    @NotNull LocalDate effectiveFrom
) {
}
