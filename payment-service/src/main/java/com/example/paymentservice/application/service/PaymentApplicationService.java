package com.example.paymentservice.application.service;

import com.example.common.query.GetByIdQuery;
import com.example.paymentservice.application.port.in.CreatePaymentCommand;
import com.example.paymentservice.application.port.in.PaymentResult;
import com.example.paymentservice.application.port.in.PaymentStatusView;
import com.example.paymentservice.application.port.in.usecase.CreatePaymentUseCase;
import com.example.paymentservice.application.port.in.usecase.GetPaymentUseCase;
import com.example.paymentservice.application.port.out.spi.PaymentEventPublisherPort;
import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.PaymentStatus;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class PaymentApplicationService implements CreatePaymentUseCase, GetPaymentUseCase {
    private final Map<UUID, Payment> payments = new ConcurrentHashMap<>();
    private final PaymentEventPublisherPort paymentEventPublisherPort;

    public PaymentApplicationService(PaymentEventPublisherPort paymentEventPublisherPort) {
        this.paymentEventPublisherPort = paymentEventPublisherPort;
    }

    @Override
    public PaymentResult createPayment(CreatePaymentCommand command) {
        Payment payment = Payment.create(command.orderId(), command.total());
        payments.put(payment.id(), payment);
        paymentEventPublisherPort.publishPaymentCreated(payment);
        return toResult(payment);
    }

    @Override
    public PaymentResult getPayment(GetByIdQuery query) {
        Payment payment = payments.get(query.id());
        if (payment == null) {
            throw new NoSuchElementException("payment not found: " + query.id());
        }
        return toResult(payment);
    }

    private PaymentResult toResult(Payment payment) {
        return new PaymentResult(
            payment.id(),
            payment.orderId(),
            payment.total().amount(),
            payment.total().currency(),
            toStatusView(payment.status())
        );
    }

    private PaymentStatusView toStatusView(PaymentStatus status) {
        return PaymentStatusView.valueOf(status.name());
    }
}
