package com.mycloud.providerservice.adapter.in.web;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.providerservice.application.port.in.AllowCustomerProviderCommand;
import com.mycloud.providerservice.application.port.in.CreateProviderCommand;
import com.mycloud.providerservice.application.port.in.ProviderResult;
import com.mycloud.providerservice.application.port.in.usecase.AllowCustomerProviderUseCase;
import com.mycloud.providerservice.application.port.in.usecase.CreateProviderUseCase;
import com.mycloud.providerservice.application.port.in.usecase.GetProviderUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/providers")
public class ProviderController {
    private final CreateProviderUseCase createProviderUseCase;
    private final GetProviderUseCase getProviderUseCase;
    private final AllowCustomerProviderUseCase allowCustomerProviderUseCase;

    public ProviderController(
        CreateProviderUseCase createProviderUseCase,
        GetProviderUseCase getProviderUseCase,
        AllowCustomerProviderUseCase allowCustomerProviderUseCase
    ) {
        this.createProviderUseCase = createProviderUseCase;
        this.getProviderUseCase = getProviderUseCase;
        this.allowCustomerProviderUseCase = allowCustomerProviderUseCase;
    }

    @PostMapping
    public ResponseEntity<ProviderResult> createProvider(@Valid @RequestBody CreateProviderCommand command) {
        ProviderResult result = createProviderUseCase.createProvider(command);
        return ResponseEntity.created(URI.create("/providers/" + result.providerId())).body(result);
    }

    @GetMapping("/{providerId}")
    public ProviderResult getProvider(@PathVariable("providerId") UUID providerId) {
        return getProviderUseCase.getProvider(new GetByIdQuery(providerId));
    }

    @PostMapping("/{providerId}/allowed-customers/{customerId}")
    public ProviderResult allowCustomer(
        @PathVariable("providerId") UUID providerId,
        @PathVariable("customerId") UUID customerId
    ) {
        return allowCustomerProviderUseCase.allowCustomer(new AllowCustomerProviderCommand(providerId, customerId));
    }
}
