package com.example.orderservice.application.port.in.usecase;

import com.example.common.query.GetByIdQuery;
import com.example.orderservice.application.port.in.OrderResult;

public interface GetOrderUseCase {
    OrderResult getOrder(GetByIdQuery query);
}
