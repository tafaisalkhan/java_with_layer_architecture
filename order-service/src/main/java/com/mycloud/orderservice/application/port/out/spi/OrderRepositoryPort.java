package com.mycloud.orderservice.application.port.out.spi;

import com.mycloud.orderservice.domain.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);

    Optional<Order> findById(UUID orderId);
}
