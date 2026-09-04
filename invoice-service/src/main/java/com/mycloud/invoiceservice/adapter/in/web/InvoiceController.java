package com.mycloud.invoiceservice.adapter.in.web;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.invoiceservice.application.port.in.BillingChargeResult;
import com.mycloud.invoiceservice.application.port.in.CreateInvoiceCommand;
import com.mycloud.invoiceservice.application.port.in.InvoiceResult;
import com.mycloud.invoiceservice.application.port.in.usecase.CreateInvoiceUseCase;
import com.mycloud.invoiceservice.application.port.in.usecase.GetCustomerBillingChargesUseCase;
import com.mycloud.invoiceservice.application.port.in.usecase.GetInvoiceUseCase;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    private final CreateInvoiceUseCase createInvoiceUseCase;
    private final GetInvoiceUseCase getInvoiceUseCase;
    private final GetCustomerBillingChargesUseCase getCustomerBillingChargesUseCase;

    public InvoiceController(
        CreateInvoiceUseCase createInvoiceUseCase,
        GetInvoiceUseCase getInvoiceUseCase,
        GetCustomerBillingChargesUseCase getCustomerBillingChargesUseCase
    ) {
        this.createInvoiceUseCase = createInvoiceUseCase;
        this.getInvoiceUseCase = getInvoiceUseCase;
        this.getCustomerBillingChargesUseCase = getCustomerBillingChargesUseCase;
    }

    @PostMapping
    public ResponseEntity<InvoiceResult> createInvoice(@Valid @RequestBody CreateInvoiceCommand command) {
        InvoiceResult result = createInvoiceUseCase.createInvoice(command);
        return ResponseEntity.created(URI.create("/invoices/" + result.invoiceId())).body(result);
    }

    @GetMapping("/{invoiceId}")
    public InvoiceResult getInvoice(@PathVariable("invoiceId") UUID invoiceId) {
        return getInvoiceUseCase.getInvoice(new GetByIdQuery(invoiceId));
    }

    @GetMapping("/customers/{customerId}/charges")
    public List<BillingChargeResult> getCustomerCharges(@PathVariable("customerId") UUID customerId) {
        return getCustomerBillingChargesUseCase.getCharges(customerId);
    }
}
