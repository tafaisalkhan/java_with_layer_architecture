package com.example.orderservice.application.port.out.spi;

import com.example.orderservice.domain.Order;

public interface OrderEventPublisherPort {
    void publishOrderCreated(Order order);
}
