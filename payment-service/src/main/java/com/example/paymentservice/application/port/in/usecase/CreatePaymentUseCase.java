package com.example.paymentservice.application.port.in.usecase;

import com.example.paymentservice.application.port.in.CreatePaymentCommand;
import com.example.paymentservice.application.port.in.PaymentResult;

public interface CreatePaymentUseCase {
    PaymentResult createPayment(CreatePaymentCommand command);
}
