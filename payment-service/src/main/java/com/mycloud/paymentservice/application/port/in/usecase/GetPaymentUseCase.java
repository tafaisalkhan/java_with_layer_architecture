package com.mycloud.paymentservice.application.port.in.usecase;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.paymentservice.application.port.in.PaymentResult;

public interface GetPaymentUseCase {
    PaymentResult getPayment(GetByIdQuery query);
}
