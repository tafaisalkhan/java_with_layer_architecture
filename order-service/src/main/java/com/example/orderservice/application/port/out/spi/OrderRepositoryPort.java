package com.example.orderservice.application.port.out.spi;

import com.example.orderservice.domain.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);

    Optional<Order> findById(UUID orderId);
}
