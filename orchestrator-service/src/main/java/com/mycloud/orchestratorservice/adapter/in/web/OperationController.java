package com.mycloud.orchestratorservice.adapter.in.web;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.orchestratorservice.application.port.in.CreateVmCommand;
import com.mycloud.orchestratorservice.application.port.in.OperationResult;
import com.mycloud.orchestratorservice.application.port.in.usecase.CreateVmOperationUseCase;
import com.mycloud.orchestratorservice.application.port.in.usecase.CreateVmOperationsUseCase;
import com.mycloud.orchestratorservice.application.port.in.usecase.GetOperationUseCase;
import com.mycloud.orchestratorservice.application.service.ProvisioningTenantAccessPolicy;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
    private final CreateVmOperationsUseCase createVmOperationsUseCase;
    private final GetOperationUseCase getOperationUseCase;
    private final ProvisioningTenantAccessPolicy tenantAccessPolicy;

    public OperationController(
        CreateVmOperationUseCase createVmOperationUseCase,
        CreateVmOperationsUseCase createVmOperationsUseCase,
        GetOperationUseCase getOperationUseCase,
        ProvisioningTenantAccessPolicy tenantAccessPolicy
    ) {
        this.createVmOperationUseCase = createVmOperationUseCase;
        this.createVmOperationsUseCase = createVmOperationsUseCase;
        this.getOperationUseCase = getOperationUseCase;
        this.tenantAccessPolicy = tenantAccessPolicy;
    }

    @PostMapping("/create-vm")
    public ResponseEntity<OperationResult> createVm(
        @Valid @RequestBody CreateVmCommand command,
        @RequestHeader(value = "X-Effective-Account-Type", required = false) String effectiveAccountType,
        @RequestHeader(value = "X-Effective-Tenant-ID", required = false) String effectiveTenantId
    ) {
        tenantAccessPolicy.ensureCanProvision(command.customerId(), effectiveAccountType, effectiveTenantId);
        OperationResult result = createVmOperationUseCase.createVm(command);
        return ResponseEntity.created(URI.create("/operations/" + result.operationId())).body(result);
    }

    @PostMapping("/create-vms")
    public ResponseEntity<List<OperationResult>> createVms(
        @Valid @RequestBody CreateVmCommand command,
        @RequestHeader(value = "X-Effective-Account-Type", required = false) String effectiveAccountType,
        @RequestHeader(value = "X-Effective-Tenant-ID", required = false) String effectiveTenantId
    ) {
        tenantAccessPolicy.ensureCanProvision(command.customerId(), effectiveAccountType, effectiveTenantId);
        List<OperationResult> results = createVmOperationsUseCase.createVms(command);
        return ResponseEntity.accepted().body(results);
    }

    @GetMapping("/{operationId}")
    public OperationResult getOperation(@PathVariable("operationId") UUID operationId) {
        return getOperationUseCase.getOperation(new GetByIdQuery(operationId));
    }
}
