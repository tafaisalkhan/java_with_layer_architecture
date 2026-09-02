package com.mycloud.customerservice.domain;

import java.util.Objects;
import java.util.UUID;

public record Customer(
    // Unique ID owned by customer-service. Stored as UUID to avoid cross-service ID collisions.
    UUID id,

    // User ID received from user-service. This service validates it through an output port.
    UUID userId,

    // Customer legal or display name.
    String fullName,

    // Customer phone number used for contact and delivery workflows.
    String phone
) {
    public Customer {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        requireText(fullName, "fullName");
        requireText(phone, "phone");
    }

    public static Customer create(UUID userId, String fullName, String phone) {
        return new Customer(UUID.randomUUID(), userId, fullName, phone);
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
