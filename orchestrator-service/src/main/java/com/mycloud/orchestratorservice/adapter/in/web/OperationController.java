package com.mycloud.orchestratorservice.adapter.in.web;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.orchestratorservice.application.port.in.CreateVmCommand;
import com.mycloud.orchestratorservice.application.port.in.OperationResult;
import com.mycloud.orchestratorservice.application.port.in.usecase.CreateVmOperationUseCase;
import com.mycloud.orchestratorservice.application.port.in.usecase.GetOperationUseCase;
import com.mycloud.orchestratorservice.application.service.ProvisioningTenantAccessPolicy;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/operations")
public class OperationController {
    private final CreateVmOperationUseCase createVmOperationUseCase;
    private final GetOperationUseCase getOperationUseCase;
    private final ProvisioningTenantAccessPolicy tenantAccessPolicy;

    public OperationController(
        CreateVmOperationUseCase createVmOperationUseCase,
        GetOperationUseCase getOperationUseCase,
        ProvisioningTenantAccessPolicy tenantAccessPolicy
    ) {
        this.createVmOperationUseCase = createVmOperationUseCase;
        this.getOperationUseCase = getOperationUseCase;
        this.tenantAccessPolicy = tenantAccessPolicy;
    }

    @PostMapping("/initiate")
    public ResponseEntity<OperationResult> initiate(
        @Valid @RequestBody InitiateOperationRequest request,
        @RequestHeader(value = "X-Effective-Account-Type", required = false) String effectiveAccountType,
        @RequestHeader(value = "X-Effective-Tenant-ID", required = false) String effectiveTenantId
    ) {
        UUID customerId = tenantAccessPolicy.effectiveCustomerId(effectiveAccountType, effectiveTenantId);
        InitiateOperationRequest.OperationDetails details = request.details();
        CreateVmCommand command = new CreateVmCommand(
            customerId,
            request.providerId(),
            request.contractId(),
            request.operationName(),
            details.resourceType(),
            details.resourceName(),
            details.imageId(),
            details.flavorId(),
            details.networkId(),
            details.priority()
        );
        OperationResult result = createVmOperationUseCase.createVm(command);
        return ResponseEntity.created(URI.create("/operations/" + result.operationId())).body(result);
    }

    @GetMapping("/{operationId}")
    public OperationResult getOperation(@PathVariable("operationId") UUID operationId) {
        return getOperationUseCase.getOperation(new GetByIdQuery(operationId));
    }
}
