package com.mycloud.orderservice.application.port.in.usecase;

import com.mycloud.orderservice.application.port.in.CreateOrderCommand;
import com.mycloud.orderservice.application.port.in.OrderResult;

public interface CreateOrderUseCase {
    OrderResult createOrder(CreateOrderCommand command);
}
