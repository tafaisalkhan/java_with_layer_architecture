package com.example.userservice.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserCommand(
    // Human-readable user name shown in profile and account screens.
    @NotBlank String name,

    // Unique email address used for login and user lookup.
    @NotBlank @Email String email
) {
}
