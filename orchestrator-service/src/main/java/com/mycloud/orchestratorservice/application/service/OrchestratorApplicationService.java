package com.mycloud.orchestratorservice.application.service;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.orchestratorservice.application.port.in.CreateVmCommand;
import com.mycloud.orchestratorservice.application.port.in.OperationResult;
import com.mycloud.orchestratorservice.application.port.in.OperationStepResult;
import com.mycloud.orchestratorservice.application.port.in.usecase.CreateVmOperationUseCase;
import com.mycloud.orchestratorservice.application.port.in.usecase.GetOperationUseCase;
import com.mycloud.orchestratorservice.application.port.out.spi.BillingPort;
import com.mycloud.orchestratorservice.application.port.out.spi.MonitoringPort;
import com.mycloud.orchestratorservice.application.port.out.spi.OperationEventPublisherPort;
import com.mycloud.orchestratorservice.application.port.out.spi.OperationRepositoryPort;
import com.mycloud.orchestratorservice.application.port.out.spi.OperationWorkflowConfigurationPort;
import com.mycloud.orchestratorservice.application.port.out.spi.ProviderConfigurationPort;
import com.mycloud.orchestratorservice.application.port.out.spi.QuotaManagementPort;
import com.mycloud.orchestratorservice.application.port.out.spi.ResourceEligibilityPort;
import com.mycloud.orchestratorservice.application.port.out.spi.ResourceProvisioningPort;
import com.mycloud.orchestratorservice.application.port.out.spi.StepExecutionPolicyPort;
import com.mycloud.orchestratorservice.application.port.out.spi.UserTokenPort;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderConfiguration;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProviderSession;
import com.mycloud.orchestratorservice.application.port.out.spi.dto.ProvisionedResource;
import com.mycloud.orchestratorservice.domain.Operation;
import com.mycloud.orchestratorservice.domain.OperationStepName;
import com.mycloud.orchestratorservice.domain.OperationType;
import com.mycloud.orchestratorservice.domain.RollbackMode;
import com.mycloud.orchestratorservice.domain.ResourceType;
import com.mycloud.orchestratorservice.domain.ResourceRequest;
import com.mycloud.orchestratorservice.domain.ProvisioningStatus;
import com.mycloud.orchestratorservice.domain.StepExecutionPolicy;
import java.net.SocketTimeoutException;
import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Value;

@Service
@Validated
public class OrchestratorApplicationService implements CreateVmOperationUseCase, GetOperationUseCase {
    private final OperationRepositoryPort operationRepositoryPort;
    private final QuotaManagementPort quotaManagementPort;
    private final ResourceEligibilityPort resourceEligibilityPort;
    private final UserTokenPort userTokenPort;
    private final ProviderConfigurationPort providerConfigurationPort;
    private final ResourceProvisioningPort resourceProvisioningPort;
    private final MonitoringPort monitoringPort;
    private final BillingPort billingPort;
    private final StepExecutionPolicyPort stepExecutionPolicyPort;
    private final OperationEventPublisherPort operationEventPublisherPort;
    private final OperationWorkflowConfigurationPort workflowConfigurationPort;
    private final Duration vmPollInterval;
    private final Duration vmBuildTimeout;

    public OrchestratorApplicationService(
        OperationRepositoryPort operationRepositoryPort,
        QuotaManagementPort quotaManagementPort,
        ResourceEligibilityPort resourceEligibilityPort,
        UserTokenPort userTokenPort,
        ProviderConfigurationPort providerConfigurationPort,
        ResourceProvisioningPort resourceProvisioningPort,
        MonitoringPort monitoringPort,
        BillingPort billingPort,
        StepExecutionPolicyPort stepExecutionPolicyPort,
        OperationEventPublisherPort operationEventPublisherPort,
        OperationWorkflowConfigurationPort workflowConfigurationPort,
        @Value("${app.provisioning.poll-interval-seconds:5}") long vmPollIntervalSeconds,
        @Value("${app.provisioning.build-timeout-seconds:900}") long vmBuildTimeoutSeconds
    ) {
        this.operationRepositoryPort = operationRepositoryPort;
        this.quotaManagementPort = quotaManagementPort;
        this.resourceEligibilityPort = resourceEligibilityPort;
        this.userTokenPort = userTokenPort;
        this.providerConfigurationPort = providerConfigurationPort;
        this.resourceProvisioningPort = resourceProvisioningPort;
        this.monitoringPort = monitoringPort;
        this.billingPort = billingPort;
        this.stepExecutionPolicyPort = stepExecutionPolicyPort;
        this.operationEventPublisherPort = operationEventPublisherPort;
        this.workflowConfigurationPort = workflowConfigurationPort;
        this.vmPollInterval = Duration.ofSeconds(vmPollIntervalSeconds);
        this.vmBuildTimeout = Duration.ofSeconds(vmBuildTimeoutSeconds);
    }

    @Override
    public OperationResult createVm(CreateVmCommand command) {
        return createPendingVmOperation(command, command.name());
    }

    private OperationResult createPendingVmOperation(CreateVmCommand command, String resourceName) {
        if (command.operationType() != OperationType.CREATE_VM || command.resourceType() != ResourceType.VM) {
            throw new IllegalArgumentException("only CREATE_VM operations for the VM resource type are currently supported");
        }
        ResourceRequest resourceRequest = new ResourceRequest(resourceName, command.imageId(), command.flavorId(), command.networkId());
        var workflow = workflowConfigurationPort.activeWorkflowFor(OperationType.CREATE_VM);
        Operation operation = Operation.createVm(
            command.customerId(),
            command.providerId(),
            command.contractId(),
            command.requestedPriority(),
            resourceRequest,
            workflow.steps().stream().map(step -> step.stepName()).toList()
        );
        operation = operationRepositoryPort.save(operation);
        return toResult(operation);
    }

    public void execute(Operation operation) {
        try {
            operation = operation.start();
            operation = operationRepositoryPort.save(operation);
            if (operation.type() != OperationType.CREATE_VM) {
                throw new IllegalArgumentException("unsupported operation type: " + operation.type());
            }
            java.util.UUID customerId = operation.customerId();
            java.util.UUID providerId = operation.providerId();
            java.util.UUID contractId = operation.contractId();
            ResourceRequest resourceRequest = operation.resourceRequest();
            operation = runParallelSteps(
                operation,
                OperationStepName.CHECK_CUSTOMER_QUOTA,
                () -> quotaManagementPort.ensureQuotaAvailable(contractId, customerId, ResourceType.VM, resourceRequest),
                OperationStepName.CHECK_RESOURCE_ELIGIBILITY,
                () -> resourceEligibilityPort.ensureEligible(customerId, providerId, ResourceType.VM, resourceRequest)
            );
            StepValue<String> token = runValueStep(operation, OperationStepName.CREATE_USER_TOKEN, () ->
                userTokenPort.createToken(customerId, providerId)
            );
            operation = token.operation();
            StepValue<ProviderConfiguration> provider = runValueStep(operation, OperationStepName.LOAD_PROVIDER_CONFIGURATION, () ->
                providerConfigurationPort.getAllowedProvider(customerId, providerId)
            );
            operation = provider.operation();
            StepValue<ProviderSession> session = runValueStep(operation, OperationStepName.PROVIDER_LOGIN, () ->
                resourceProvisioningPort.login(provider.value(), token.value())
            );
            operation = session.operation();
            StepValue<String> resourceId = operation.provisionedResourceId() == null
                ? runValueStep(operation, OperationStepName.PROVISION_RESOURCE, () ->
                    resourceProvisioningPort.createVm(provider.value(), session.value(), resourceRequest)
                )
                : new StepValue<>(operation, operation.provisionedResourceId());
            operation = operationRepositoryPort.save(resourceId.operation().withProvisionedResourceId(resourceId.value()));
            operation = waitForVmActive(operation, provider.value(), session.value(), resourceId.value());
            operation = runStep(operation, OperationStepName.ASSIGN_PUBLIC_IP, () ->
                resourceProvisioningPort.assignVmAccess(provider.value(), session.value(), resourceId.value())
            );
            StepValue<ProvisionedResource> provisionedResource = runValueStep(operation, OperationStepName.COLLECT_RESOURCE_METADATA, () ->
                resourceProvisioningPort.collectVmMetadata(provider.value(), session.value(), resourceId.value())
            );
            operation = provisionedResource.operation().succeed(provisionedResource.value().resourceId());
            Operation monitorableOperation = operation;
            operation = runStep(operation, OperationStepName.REGISTER_MONITORING, () ->
                monitoringPort.registerVm(monitorableOperation, provisionedResource.value())
            );
            Operation quotaCommitOperation = operation;
            operation = runStep(operation, OperationStepName.COMMIT_QUOTA, () ->
                quotaManagementPort.commitQuota(quotaCommitOperation.contractId(), quotaCommitOperation.customerId(), quotaCommitOperation.resourceType(), quotaCommitOperation.resourceRequest())
            );
            Operation billableOperation = operation;
            operation = runStep(operation, OperationStepName.REQUEST_BILLING, () -> billingPort.requestBilling(billableOperation));
            operation = operationRepositoryPort.save(operation);
            operationEventPublisherPort.publishSucceeded(operation);
        } catch (StepExecutionException exception) {
            handleStepFailure(exception);
        } catch (PollingDeferredException ignored) {
            // State and next poll time are persisted; the scheduler will resume this operation.
        } catch (RuntimeException exception) {
            Operation failed = operation.failStep(currentStep(operation), exception.getMessage(), Instant.now());
            failed = operationRepositoryPort.save(failed);
            operationEventPublisherPort.publishFailed(failed);
        }
    }

    private Operation waitForVmActive(Operation operation, ProviderConfiguration provider, ProviderSession session, String resourceId) {
        Operation running = operationRepositoryPort.save(operation.startStep(OperationStepName.WAIT_FOR_VM_ACTIVE, Instant.now()));
        Instant now = Instant.now();
        Instant pollingStartedAt = running.steps().stream()
            .filter(step -> step.name() == OperationStepName.WAIT_FOR_VM_ACTIVE)
            .findFirst()
            .map(step -> step.startedAt())
            .orElse(now);
        if (Duration.between(pollingStartedAt, now).compareTo(vmBuildTimeout) >= 0) {
            throw new StepExecutionException(running, OperationStepName.WAIT_FOR_VM_ACTIVE,
                "VM did not become active within " + vmBuildTimeout.toSeconds() + " seconds", false);
        }
        ProvisioningStatus status = resourceProvisioningPort.getVmStatus(provider, session, resourceId);
        if (status == ProvisioningStatus.ACTIVE) {
            return operationRepositoryPort.save(running.succeedStep(OperationStepName.WAIT_FOR_VM_ACTIVE, now, status.name()));
        }
        if (status == ProvisioningStatus.FAILED || status == ProvisioningStatus.DELETED) {
            throw new StepExecutionException(running, OperationStepName.WAIT_FOR_VM_ACTIVE,
                "provider reported VM status " + status, false);
        }
        operationRepositoryPort.save(running.waitForStepPoll(OperationStepName.WAIT_FOR_VM_ACTIVE,
            now, now.plus(vmPollInterval), status.name()));
        throw new PollingDeferredException();
    }

    @Override
    public OperationResult getOperation(GetByIdQuery query) {
        return operationRepositoryPort.findById(query.id())
            .map(this::toResult)
            .orElseThrow(() -> new NoSuchElementException("operation not found: " + query.id()));
    }

    private Operation runStep(Operation operation, OperationStepName stepName, Runnable action) {
        if (stepSucceeded(operation, stepName)) {
            return operation;
        }
        Operation running = operationRepositoryPort.save(operation.startStep(stepName, Instant.now()));
        try {
            action.run();
        } catch (RuntimeException exception) {
            StepExecutionPolicy policy = stepExecutionPolicyPort.policyFor(stepName);
            if (!policy.requiredStep()) {
                return operationRepositoryPort.save(running.failStepOnly(stepName, exceptionMessage(exception), Instant.now()));
            }
            throw new StepExecutionException(running, stepName, exceptionMessage(exception), isRetryable(exception));
        }
        return operationRepositoryPort.save(running.succeedStep(stepName, Instant.now()));
    }

    private <T> StepValue<T> runValueStep(Operation operation, OperationStepName stepName, java.util.function.Supplier<T> action) {
        if (stepSucceeded(operation, stepName)) {
            return new StepValue<>(operation, action.get());
        }
        Operation running = operationRepositoryPort.save(operation.startStep(stepName, Instant.now()));
        T value;
        try {
            value = action.get();
        } catch (RuntimeException exception) {
            throw new StepExecutionException(running, stepName, exceptionMessage(exception), isRetryable(exception));
        }
        return new StepValue<>(operationRepositoryPort.save(running.succeedStep(stepName, Instant.now())), value);
    }

    private Operation runParallelSteps(
        Operation operation,
        OperationStepName firstStepName,
        Runnable firstAction,
        OperationStepName secondStepName,
        Runnable secondAction
    ) {
        Operation running = operationRepositoryPort.save(
            operation.startStep(firstStepName, Instant.now()).startStep(secondStepName, Instant.now())
        );
        CompletableFuture<Void> first = stepSucceeded(operation, firstStepName)
            ? CompletableFuture.completedFuture(null) : CompletableFuture.runAsync(firstAction);
        CompletableFuture<Void> second = stepSucceeded(operation, secondStepName)
            ? CompletableFuture.completedFuture(null) : CompletableFuture.runAsync(secondAction);
        try {
            CompletableFuture.allOf(first, second).join();
        } catch (CompletionException ignored) {
            // Individual futures are inspected below so the failed step is recorded accurately.
        }
        StepExecutionException failure = null;
        try {
            running = finishParallelStep(running, firstStepName, first);
        } catch (StepExecutionException exception) {
            failure = exception;
        }
        try {
            running = finishParallelStep(running, secondStepName, second);
        } catch (StepExecutionException exception) {
            if (failure == null) {
                failure = exception;
            }
        }
        if (failure != null) {
            throw new StepExecutionException(running, failure.stepName(), failure.getMessage(), failure.retryable());
        }
        return running;
    }

    private Operation finishParallelStep(Operation operation, OperationStepName stepName, CompletableFuture<Void> future) {
        if (stepSucceeded(operation, stepName)) {
            return operation;
        }
        try {
            future.join();
            return operationRepositoryPort.save(operation.succeedStep(stepName, Instant.now()));
        } catch (CompletionException exception) {
            throw new StepExecutionException(operation, stepName, exceptionMessage(exception), isRetryable(exception));
        }
    }

    private boolean stepSucceeded(Operation operation, OperationStepName stepName) {
        return operation.steps().stream().anyMatch(step -> step.name() == stepName
            && step.status() == com.mycloud.orchestratorservice.domain.OperationStepStatus.SUCCEEDED);
    }

    private void handleStepFailure(StepExecutionException exception) {
        Instant now = Instant.now();
        Operation operation = exception.operation();
        StepExecutionPolicy policy = stepExecutionPolicyPort.policyFor(exception.stepName());
        int attemptCount = operation.steps().stream()
            .filter(step -> step.name() == exception.stepName())
            .findFirst()
            .map(step -> step.attemptCount())
            .orElse(1);

        if (exception.retryable() && policy.retryEnabled() && attemptCount < policy.maxAttempts()) {
            Operation retry = operation.retryStepLater(
                exception.stepName(),
                exception.getMessage(),
                now,
                now.plus(policy.retryDelay())
            );
            operationRepositoryPort.save(retry);
            return;
        }

        var workflow = workflowConfigurationPort.activeWorkflowFor(operation.type());
        if (workflow.rollbackMode() == RollbackMode.AUTO) {
            rollback(operation, exception, now, workflow.quotaRollbackEnabled());
            return;
        }
        if (workflow.rollbackMode() == RollbackMode.MANUAL) {
            Operation waiting = operationRepositoryPort.save(operation.failStepOnly(
                exception.stepName(), exception.getMessage(), now).rollbackRequired(exception.getMessage()));
            operationEventPublisherPort.publishFailed(waiting);
            return;
        }

        Operation failed = operation.failStep(exception.stepName(), exception.getMessage(), now);
        failed = operationRepositoryPort.save(failed);
        operationEventPublisherPort.publishFailed(failed);
    }

    private void rollback(Operation operation, StepExecutionException exception, Instant now, boolean restoreQuota) {
        Operation rollingBack = operationRepositoryPort.save(operation.startRollback(exception.getMessage()));
        try {
            if (rollingBack.provisionedResourceId() != null) {
                ProviderConfiguration providerConfiguration = providerConfigurationPort.getAllowedProvider(
                    rollingBack.customerId(),
                    rollingBack.providerId()
            );
            String token = userTokenPort.createToken(rollingBack.customerId(), rollingBack.providerId());
            ProviderSession session = resourceProvisioningPort.login(providerConfiguration, token);
                String provisionedResourceId = rollingBack.provisionedResourceId();
                rollingBack = runStep(rollingBack, OperationStepName.ROLLBACK_RESOURCE, () ->
                    resourceProvisioningPort.rollbackVm(providerConfiguration, session, provisionedResourceId)
                );
            }
            if (restoreQuota) {
                quotaManagementPort.releaseQuota(rollingBack.contractId(), rollingBack.customerId(), rollingBack.resourceType(), rollingBack.resourceRequest());
            }
            Operation rolledBack = operationRepositoryPort.save(rollingBack.rolledBack());
            operationEventPublisherPort.publishFailed(rolledBack);
        } catch (RuntimeException rollbackException) {
            Operation failed = rollingBack.rollbackFailed(exception.getMessage() + "; rollback failed: " + exceptionMessage(rollbackException));
            failed = operationRepositoryPort.save(failed);
            operationEventPublisherPort.publishFailed(failed);
        }
    }

    private boolean isRetryable(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof ResourceAccessException || current instanceof SocketTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private String exceptionMessage(Throwable exception) {
        Throwable cause = exception instanceof CompletionException && exception.getCause() != null
            ? exception.getCause()
            : exception;
        return cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
    }

    private OperationStepName currentStep(Operation operation) {
        return operation.steps().stream()
            .filter(step -> step.status().name().equals("RUNNING"))
            .findFirst()
            .map(step -> step.name())
            .orElse(OperationStepName.PROVISION_RESOURCE);
    }

    private OperationResult toResult(Operation operation) {
        return new OperationResult(
            operation.id(),
            operation.customerId(),
            operation.providerId(),
            operation.contractId(),
            operation.type().name(),
            operation.resourceType().name(),
            operation.priority().name(),
            operation.status().name(),
            operation.provisionedResourceId(),
            operation.failureReason(),
            operation.steps().stream()
                .map(step -> new OperationStepResult(
                    step.name().name(),
                    step.status().name(),
                    step.failureReason(),
                    step.startedAt(),
                    step.finishedAt(),
                    step.attemptCount(),
                    step.nextRetryAt(),
                    step.lastCheckedAt(),
                    step.providerStatus()
                ))
                .toList()
        );
    }

    private record StepValue<T>(Operation operation, T value) {
    }

    private static class StepExecutionException extends RuntimeException {
        private final Operation operation;
        private final OperationStepName stepName;
        private final boolean retryable;

        StepExecutionException(Operation operation, OperationStepName stepName, String message, boolean retryable) {
            super(message);
            this.operation = operation;
            this.stepName = stepName;
            this.retryable = retryable;
        }

        Operation operation() {
            return operation;
        }

        OperationStepName stepName() {
            return stepName;
        }

        boolean retryable() {
            return retryable;
        }
    }

    private static class PollingDeferredException extends RuntimeException {
    }
}
