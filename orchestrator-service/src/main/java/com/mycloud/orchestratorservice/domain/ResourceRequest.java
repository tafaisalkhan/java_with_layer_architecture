package com.mycloud.orchestratorservice.domain;

import java.util.Objects;

public record ResourceRequest(
    String name,
    String imageId,
    String flavorId,
    String networkId
) {
    public ResourceRequest {
        requireText(name, "name");
        requireText(imageId, "imageId");
        requireText(flavorId, "flavorId");
        requireText(networkId, "networkId");
    }

    private static void requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
