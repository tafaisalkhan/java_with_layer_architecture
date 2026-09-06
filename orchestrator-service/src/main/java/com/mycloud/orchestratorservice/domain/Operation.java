package com.mycloud.orchestratorservice.domain;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record Operation(
    UUID id,
    UUID customerId,
    UUID providerId,
    UUID contractId,
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

    public static Operation createVm(
        UUID customerId,
        UUID providerId,
        UUID contractId,
        OperationPriority priority,
        ResourceRequest request,
        List<OperationStepName> configuredSteps
    ) {
        return new Operation(
            UUID.randomUUID(),
            customerId,
            providerId,
            contractId,
            OperationType.CREATE_VM,
            ResourceType.VM,
            priority,
            request,
            OperationStatus.PENDING,
            null,
            null,
            configuredSteps.stream().map(OperationStep::pending).toList()
        );
    }

    public Operation start() {
        return new Operation(id, customerId, providerId, contractId, type, resourceType, priority, resourceRequest, OperationStatus.RUNNING, provisionedResourceId, failureReason, steps);
    }

    public Operation startStep(OperationStepName stepName, Instant now) {
        return replaceStep(stepName, step -> step.start(now));
    }

    public Operation succeedStep(OperationStepName stepName, Instant now) {
        return replaceStep(stepName, step -> step.succeed(now));
    }

    public Operation succeedStep(OperationStepName stepName, Instant now, String providerStatus) {
        return replaceStep(stepName, step -> step.succeed(now, providerStatus));
    }

    public Operation failStep(OperationStepName stepName, String reason, Instant now) {
        Operation failed = replaceStep(stepName, step -> step.fail(reason, now));
        return new Operation(id, customerId, providerId, contractId, type, resourceType, priority, resourceRequest, OperationStatus.FAILED, provisionedResourceId, reason, failed.steps);
    }

    public Operation failStepOnly(OperationStepName stepName, String reason, Instant now) {
        return replaceStep(stepName, step -> step.fail(reason, now));
    }

    public Operation retryStepLater(OperationStepName stepName, String reason, Instant now, Instant nextRetryAt) {
        Operation retry = replaceStep(stepName, step -> step.retryLater(reason, now, nextRetryAt));
        return new Operation(id, customerId, providerId, contractId, type, resourceType, priority, resourceRequest, OperationStatus.PENDING, provisionedResourceId, reason, retry.steps);
    }

    public Operation waitForStepPoll(OperationStepName stepName, Instant now, Instant nextPollAt, String providerStatus) {
        Operation waiting = replaceStep(stepName, step -> step.waitForPoll(now, nextPollAt, providerStatus));
        return new Operation(id, customerId, providerId, contractId, type, resourceType, priority, resourceRequest,
            OperationStatus.WAITING, provisionedResourceId, failureReason, waiting.steps);
    }

    public Operation succeed(String resourceId) {
        return new Operation(id, customerId, providerId, contractId, type, resourceType, priority, resourceRequest, OperationStatus.SUCCEEDED, resourceId, null, steps);
    }

    public Operation withProvisionedResourceId(String resourceId) {
        return new Operation(id, customerId, providerId, contractId, type, resourceType, priority, resourceRequest, status, resourceId, failureReason, steps);
    }

    public Operation startRollback(String reason) {
        return new Operation(id, customerId, providerId, contractId, type, resourceType, priority, resourceRequest, OperationStatus.ROLLING_BACK, provisionedResourceId, reason, steps);
    }

    public Operation rolledBack() {
        return new Operation(id, customerId, providerId, contractId, type, resourceType, priority, resourceRequest, OperationStatus.ROLLED_BACK, provisionedResourceId, failureReason, steps);
    }

    public Operation rollbackRequired(String reason) {
        return new Operation(id, customerId, providerId, contractId, type, resourceType, priority, resourceRequest,
            OperationStatus.ROLLBACK_REQUIRED, provisionedResourceId, reason, steps);
    }

    public Operation rollbackFailed(String reason) {
        return new Operation(id, customerId, providerId, contractId, type, resourceType, priority, resourceRequest,
            OperationStatus.ROLLBACK_FAILED, provisionedResourceId, reason, steps);
    }

    private Operation replaceStep(OperationStepName stepName, java.util.function.Function<OperationStep, OperationStep> update) {
        return new Operation(
            id,
            customerId,
            providerId,
            contractId,
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
