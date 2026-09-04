package com.mycloud.orchestratorservice.adapter.out.persistence;

import com.mycloud.orchestratorservice.domain.OperationStatus;
import com.mycloud.orchestratorservice.domain.OperationPriority;
import com.mycloud.orchestratorservice.domain.OperationType;
import com.mycloud.orchestratorservice.domain.ResourceType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "operations")
public class OperationJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 32)
    private OperationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", nullable = false, length = 32)
    private ResourceType resourceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 32)
    private OperationPriority priority;

    @Column(name = "resource_name", nullable = false)
    private String resourceName;

    @Column(name = "image_id", nullable = false)
    private String imageId;

    @Column(name = "flavor_id", nullable = false)
    private String flavorId;

    @Column(name = "network_id", nullable = false)
    private String networkId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private OperationStatus status;

    @Column(name = "provisioned_resource_id")
    private String provisionedResourceId;

    @Column(name = "failure_reason")
    private String failureReason;

    @ElementCollection
    @CollectionTable(name = "operation_steps", joinColumns = @JoinColumn(name = "operation_id"))
    private List<OperationStepJpaEmbeddable> steps = new ArrayList<>();

    protected OperationJpaEntity() {
    }

    public OperationJpaEntity(
        UUID id,
        UUID customerId,
        UUID providerId,
        OperationType type,
        ResourceType resourceType,
        OperationPriority priority,
        String resourceName,
        String imageId,
        String flavorId,
        String networkId,
        OperationStatus status,
        String provisionedResourceId,
        String failureReason,
        List<OperationStepJpaEmbeddable> steps
    ) {
        this.id = id;
        this.customerId = customerId;
        this.providerId = providerId;
        this.type = type;
        this.resourceType = resourceType;
        this.priority = priority;
        this.resourceName = resourceName;
        this.imageId = imageId;
        this.flavorId = flavorId;
        this.networkId = networkId;
        this.status = status;
        this.provisionedResourceId = provisionedResourceId;
        this.failureReason = failureReason;
        this.steps = new ArrayList<>(steps);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public OperationType getType() {
        return type;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public OperationPriority getPriority() {
        return priority;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getImageId() {
        return imageId;
    }

    public String getFlavorId() {
        return flavorId;
    }

    public String getNetworkId() {
        return networkId;
    }

    public OperationStatus getStatus() {
        return status;
    }

    public String getProvisionedResourceId() {
        return provisionedResourceId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public List<OperationStepJpaEmbeddable> getSteps() {
        return steps;
    }
}
