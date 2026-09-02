package com.mycloud.orderservice.application.port.in.usecase;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.orderservice.application.port.in.OrderResult;

public interface GetOrderUseCase {
    OrderResult getOrder(GetByIdQuery query);
}
