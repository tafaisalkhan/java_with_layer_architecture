package com.mycloud.paymentservice.application.port.out.spi;

import com.mycloud.paymentservice.domain.Payment;

public interface PaymentEventPublisherPort {
    void publishPaymentCreated(Payment payment);
}
