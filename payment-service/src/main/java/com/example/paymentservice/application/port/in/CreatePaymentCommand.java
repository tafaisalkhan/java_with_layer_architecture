package com.example.paymentservice.application.port.in;

import com.example.common.money.MoneyValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreatePaymentCommand(
    // Order ID from order-service. Payment-service stores it to connect payment and order.
    @NotNull UUID orderId,

    // Payment value. Contains amount and currency as one validated object.
    @Valid @NotNull MoneyValue total
) {
}
