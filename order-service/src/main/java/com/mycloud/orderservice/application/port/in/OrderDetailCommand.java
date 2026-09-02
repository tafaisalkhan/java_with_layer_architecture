package com.mycloud.orderservice.application.port.in;

import com.mycloud.common.money.MoneyValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderDetailCommand(
    @NotBlank String productName,
    @Min(1) int quantity,
    @Valid @NotNull MoneyValue unitPrice
) {
}
