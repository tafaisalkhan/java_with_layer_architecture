package com.mycloud.orchestratorservice.adapter.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycloud.orchestratorservice.domain.OperationPriority;
import com.mycloud.orchestratorservice.domain.OperationType;
import com.mycloud.orchestratorservice.domain.ResourceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Public API request. Customer identity is intentionally derived from the authenticated token. */
public record InitiateOperationRequest(
    @NotNull @JsonProperty("contract_id") UUID contractId,
    @NotNull @JsonProperty("provider_id") UUID providerId,
    @NotNull @JsonProperty("operation_name") OperationType operationName,
    @NotNull @Valid OperationDetails details
) {
    public record OperationDetails(
        @NotNull @JsonProperty("resource_type") ResourceType resourceType,
        @NotBlank @JsonProperty("resource_name") String resourceName,
        @NotBlank @JsonProperty("image_id") String imageId,
        @NotBlank @JsonProperty("flavor_id") String flavorId,
        @NotBlank @JsonProperty("network_id") String networkId,
        OperationPriority priority
    ) {
    }
}
