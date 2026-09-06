package com.mycloud.customerservice.adapter.in.web;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.customerservice.application.port.in.CreateCustomerCommand;
import com.mycloud.customerservice.application.port.in.CustomerResult;
import com.mycloud.customerservice.application.port.in.usecase.CreateCustomerUseCase;
import com.mycloud.customerservice.application.port.in.usecase.GetCustomerUseCase;
import com.mycloud.customerservice.application.service.CustomerTenantAccessPolicy;
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
@RequestMapping("/customers")
public class CustomerController {
    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final CustomerTenantAccessPolicy tenantAccessPolicy;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase, GetCustomerUseCase getCustomerUseCase,
                              CustomerTenantAccessPolicy tenantAccessPolicy) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.tenantAccessPolicy = tenantAccessPolicy;
    }

    @PostMapping
    public ResponseEntity<CustomerResult> createCustomer(@Valid @RequestBody CreateCustomerCommand command) {
        CustomerResult result = createCustomerUseCase.createCustomer(command);
        return ResponseEntity.created(URI.create("/customers/" + result.customerId())).body(result);
    }

    @GetMapping("/{customerId}")
    public CustomerResult getCustomer(
        @PathVariable("customerId") UUID customerId,
        @RequestHeader(value = "X-Effective-Account-Type", required = false) String effectiveAccountType,
        @RequestHeader(value = "X-Effective-Tenant-ID", required = false) String effectiveTenantId
    ) {
        tenantAccessPolicy.ensureCanReadCustomer(customerId, effectiveAccountType, effectiveTenantId);
        return getCustomerUseCase.getCustomer(new GetByIdQuery(customerId));
    }
}
