package com.mycloud.providerservice.domain;

public record ProviderEndpoint(
    String serviceName,
    String url
) {
    public ProviderEndpoint {
        requireText(serviceName, "serviceName");
        requireText(url, "url");
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
