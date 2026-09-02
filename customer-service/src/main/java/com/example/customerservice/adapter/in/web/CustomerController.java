package com.example.customerservice.adapter.in.web;

import com.example.common.query.GetByIdQuery;
import com.example.customerservice.application.port.in.CreateCustomerCommand;
import com.example.customerservice.application.port.in.CustomerResult;
import com.example.customerservice.application.port.in.usecase.CreateCustomerUseCase;
import com.example.customerservice.application.port.in.usecase.GetCustomerUseCase;
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
@RequestMapping("/customers")
public class CustomerController {
    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase, GetCustomerUseCase getCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
    }

    @PostMapping
    public ResponseEntity<CustomerResult> createCustomer(@Valid @RequestBody CreateCustomerCommand command) {
        CustomerResult result = createCustomerUseCase.createCustomer(command);
        return ResponseEntity.created(URI.create("/customers/" + result.customerId())).body(result);
    }

    @GetMapping("/{customerId}")
    public CustomerResult getCustomer(@PathVariable("customerId") UUID customerId) {
        return getCustomerUseCase.getCustomer(new GetByIdQuery(customerId));
    }
}
