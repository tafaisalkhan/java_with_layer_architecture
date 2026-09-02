package com.mycloud.paymentservice.application.port.in.usecase;

import com.mycloud.paymentservice.application.port.in.CreatePaymentCommand;
import com.mycloud.paymentservice.application.port.in.PaymentResult;

public interface CreatePaymentUseCase {
    PaymentResult createPayment(CreatePaymentCommand command);
}
