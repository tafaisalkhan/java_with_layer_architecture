package com.mycloud.orderservice.application.port.in;

import com.mycloud.common.money.MoneyValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
    // Customer ID from customer-service. Order-service validates it through an output port later.
    @NotNull UUID customerId,

    // Total order value. Contains amount and currency as one validated object.
    @Valid @NotNull MoneyValue total,

    // Line-level order details that belong to this order.
    @Valid @NotEmpty List<OrderDetailCommand> details
) {
}
