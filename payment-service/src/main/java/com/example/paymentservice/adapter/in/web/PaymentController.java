package com.example.paymentservice.adapter.in.web;

import com.example.common.query.GetByIdQuery;
import com.example.paymentservice.application.port.in.CreatePaymentCommand;
import com.example.paymentservice.application.port.in.PaymentResult;
import com.example.paymentservice.application.port.in.usecase.CreatePaymentUseCase;
import com.example.paymentservice.application.port.in.usecase.GetPaymentUseCase;
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
@RequestMapping("/payments")
public class PaymentController {
    private final CreatePaymentUseCase createPaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;

    public PaymentController(CreatePaymentUseCase createPaymentUseCase, GetPaymentUseCase getPaymentUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.getPaymentUseCase = getPaymentUseCase;
    }

    @PostMapping
    public ResponseEntity<PaymentResult> createPayment(@Valid @RequestBody CreatePaymentCommand command) {
        PaymentResult result = createPaymentUseCase.createPayment(command);
        return ResponseEntity.created(URI.create("/payments/" + result.paymentId())).body(result);
    }

    @GetMapping("/{paymentId}")
    public PaymentResult getPayment(@PathVariable("paymentId") UUID paymentId) {
        return getPaymentUseCase.getPayment(new GetByIdQuery(paymentId));
    }
}
