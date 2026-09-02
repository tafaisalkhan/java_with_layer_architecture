package com.example.orderservice.application.port.in.usecase;

import com.example.orderservice.application.port.in.CreateOrderCommand;
import com.example.orderservice.application.port.in.OrderResult;

public interface CreateOrderUseCase {
    OrderResult createOrder(CreateOrderCommand command);
}
