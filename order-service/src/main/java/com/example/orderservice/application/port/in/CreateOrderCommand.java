package com.example.orderservice.application.port.in;

import com.example.common.money.MoneyValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateOrderCommand(
    // Customer ID from customer-service. Order-service validates it through an output port later.
    @NotNull UUID customerId,

    // Total order value. Contains amount and currency as one validated object.
    @Valid @NotNull MoneyValue total
) {
}
