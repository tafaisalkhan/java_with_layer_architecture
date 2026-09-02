package com.example.paymentservice.application.port.out.spi;

import com.example.paymentservice.domain.Payment;

public interface PaymentEventPublisherPort {
    void publishPaymentCreated(Payment payment);
}
