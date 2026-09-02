package com.example.userservice.application.port.in;

import java.util.UUID;

public record UserResult(
    // Unique user ID owned by user-service.
    UUID userId,

    // Human-readable user name.
    String name,

    // Email address registered for this user.
    String email
) {
}
