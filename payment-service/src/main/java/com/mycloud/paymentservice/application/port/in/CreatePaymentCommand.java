package com.mycloud.paymentservice.application.port.in;

import com.mycloud.common.money.MoneyValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreatePaymentCommand(
    // Invoice ID from invoice-service. Payment-service stores it to connect payment and invoice.
    @NotNull UUID invoiceId,

    // Payment value. Contains amount and currency as one validated object.
    @Valid @NotNull MoneyValue total
) {
}
