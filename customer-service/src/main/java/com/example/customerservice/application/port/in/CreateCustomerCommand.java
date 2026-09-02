package com.example.customerservice.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateCustomerCommand(
    // User ID from user-service. Customer-service validates it through an output port later.
    @NotNull UUID userId,

    // Customer legal or display name.
    @NotBlank String fullName,

    // Customer phone number used for contact and delivery workflows.
    @NotBlank String phone
) {
}
