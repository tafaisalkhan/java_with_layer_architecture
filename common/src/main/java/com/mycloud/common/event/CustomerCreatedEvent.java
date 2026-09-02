package com.mycloud.common.event;

import java.util.UUID;

public record CustomerCreatedEvent(
    UUID customerId,
    UUID userId,
    String fullName,
    String phone
) {
}
