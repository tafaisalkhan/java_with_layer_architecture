package com.example.userservice.domain;

import java.util.Objects;
import java.util.UUID;

public record User(
    // Unique ID owned by user-service. Stored as UUID to avoid cross-service ID collisions.
    UUID id,

    // Human-readable user name shown in profile and account screens.
    String name,

    // Unique email address used for login and user lookup.
    String email
) {
    public User {
        Objects.requireNonNull(id, "id must not be null");
        requireText(name, "name");
        requireText(email, "email");
    }

    public static User create(String name, String email) {
        return new User(UUID.randomUUID(), name, email);
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
