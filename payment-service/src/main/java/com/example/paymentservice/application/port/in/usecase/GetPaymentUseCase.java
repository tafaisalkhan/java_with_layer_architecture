package com.example.paymentservice.application.port.in.usecase;

import com.example.common.query.GetByIdQuery;
import com.example.paymentservice.application.port.in.PaymentResult;

public interface GetPaymentUseCase {
    PaymentResult getPayment(GetByIdQuery query);
}
