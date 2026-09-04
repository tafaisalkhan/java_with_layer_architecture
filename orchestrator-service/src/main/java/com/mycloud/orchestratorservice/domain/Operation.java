package com.mycloud.orchestratorservice.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Operation(
    UUID id,
    UUID customerId,
    UUID providerId,
    OperationType type,
    ResourceType resourceType,
    OperationPriority priority,
    ResourceRequest resourceRequest,
    OperationStatus status,
    String provisionedResourceId,
    String failureReason,
    List<OperationStep> steps
) {
    public Operation {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(providerId, "providerId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(resourceType, "resourceType must not be null");
        Objects.requireNonNull(priority, "priority must not be null");
        Objects.requireNonNull(resourceRequest, "resourceRequest must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(steps, "steps must not be null");
        steps = List.copyOf(steps);
    }

    public static Operation createVm(UUID customerId, UUID providerId, OperationPriority priority, ResourceRequest request) {
        return new Operation(
            UUID.randomUUID(),
            customerId,
            providerId,
            OperationType.CREATE_VM,
            ResourceType.VM,
            priority,
            request,
            OperationStatus.PENDING,
            null,
            null,
            List.of(
                OperationStep.pending(OperationStepName.CHECK_CUSTOMER_QUOTA),
                OperationStep.pending(OperationStepName.CHECK_RESOURCE_ELIGIBILITY),
                OperationStep.pending(OperationStepName.CREATE_USER_TOKEN),
                OperationStep.pending(OperationStepName.LOAD_PROVIDER_CONFIGURATION),
                OperationStep.pending(OperationStepName.PROVIDER_LOGIN),
                OperationStep.pending(OperationStepName.PROVISION_RESOURCE),
                OperationStep.pending(OperationStepName.ASSIGN_PUBLIC_IP),
                OperationStep.pending(OperationStepName.COLLECT_RESOURCE_METADATA),
                OperationStep.pending(OperationStepName.REGISTER_MONITORING),
                OperationStep.pending(OperationStepName.COMMIT_QUOTA),
                OperationStep.pending(OperationStepName.REQUEST_BILLING),
                OperationStep.pending(OperationStepName.ROLLBACK_RESOURCE),
                OperationStep.pending(OperationStepName.RELEASE_QUOTA)
            )
        );
    }

    public Operation start() {
        return new Operation(id, customerId, providerId, type, resourceType, priority, resourceRequest, OperationStatus.RUNNING, provisionedResourceId, failureReason, steps);
    }

    public Operation startStep(OperationStepName stepName, Instant now) {
        return replaceStep(stepName, step -> step.start(now));
    }

    public Operation succeedStep(OperationStepName stepName, Instant now) {
        return replaceStep(stepName, step -> step.succeed(now));
    }

    public Operation failStep(OperationStepName stepName, String reason, Instant now) {
        Operation failed = replaceStep(stepName, step -> step.fail(reason, now));
        return new Operation(id, customerId, providerId, type, resourceType, priority, resourceRequest, OperationStatus.FAILED, provisionedResourceId, reason, failed.steps);
    }

    public Operation failStepOnly(OperationStepName stepName, String reason, Instant now) {
        return replaceStep(stepName, step -> step.fail(reason, now));
    }

    public Operation retryStepLater(OperationStepName stepName, String reason, Instant now, Instant nextRetryAt) {
        Operation retry = replaceStep(stepName, step -> step.retryLater(reason, now, nextRetryAt));
        return new Operation(id, customerId, providerId, type, resourceType, priority, resourceRequest, OperationStatus.PENDING, provisionedResourceId, reason, retry.steps);
    }

    public Operation succeed(String resourceId) {
        return new Operation(id, customerId, providerId, type, resourceType, priority, resourceRequest, OperationStatus.SUCCEEDED, resourceId, null, steps);
    }

    public Operation withProvisionedResourceId(String resourceId) {
        return new Operation(id, customerId, providerId, type, resourceType, priority, resourceRequest, status, resourceId, failureReason, steps);
    }

    public Operation startRollback(String reason) {
        return new Operation(id, customerId, providerId, type, resourceType, priority, resourceRequest, OperationStatus.ROLLING_BACK, provisionedResourceId, reason, steps);
    }

    public Operation rolledBack() {
        return new Operation(id, customerId, providerId, type, resourceType, priority, resourceRequest, OperationStatus.ROLLED_BACK, provisionedResourceId, failureReason, steps);
    }

    private Operation replaceStep(OperationStepName stepName, java.util.function.Function<OperationStep, OperationStep> update) {
        return new Operation(
            id,
            customerId,
            providerId,
            type,
            resourceType,
            priority,
            resourceRequest,
            status,
            provisionedResourceId,
            failureReason,
            steps.stream()
                .map(step -> step.name() == stepName ? update.apply(step) : step)
                .toList()
        );
    }
}
