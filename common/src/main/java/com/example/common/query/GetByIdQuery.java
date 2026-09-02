package com.example.common.query;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetByIdQuery(
    // Service-owned unique identifier for the object being requested.
    @NotNull UUID id
) {
}
