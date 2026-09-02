package com.mycloud.customerservice.application.port.in;

import java.util.UUID;

public record CustomerResult(
    // Unique customer ID owned by customer-service.
    UUID customerId,

    // User ID from user-service connected to this customer profile.
    UUID userId,

    // Customer legal or display name.
    String fullName,

    // Customer phone number.
    String phone
) {
}
