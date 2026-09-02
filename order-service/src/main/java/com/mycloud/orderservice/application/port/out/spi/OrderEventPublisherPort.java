package com.mycloud.orderservice.application.port.out.spi;

import com.mycloud.orderservice.domain.Order;

public interface OrderEventPublisherPort {
    void publishOrderCreated(Order order);
}
