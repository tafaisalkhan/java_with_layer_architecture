package com.example.orderservice.adapter.out.persistence;

import com.example.common.money.MoneyValue;
import com.example.orderservice.adapter.out.persistence.repository.SpringDataOrderRepository;
import com.example.orderservice.application.port.out.spi.OrderRepositoryPort;
import com.example.orderservice.domain.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOrderPersistenceAdapter implements OrderRepositoryPort {
    private final SpringDataOrderRepository springDataOrderRepository;

    public JpaOrderPersistenceAdapter(SpringDataOrderRepository springDataOrderRepository) {
        this.springDataOrderRepository = springDataOrderRepository;
    }

    @Override
    public Order save(Order order) {
        return toDomain(springDataOrderRepository.save(toEntity(order)));
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return springDataOrderRepository.findById(orderId).map(this::toDomain);
    }

    private OrderJpaEntity toEntity(Order order) {
        return new OrderJpaEntity(
            order.id(),
            order.customerId(),
            order.paymentId(),
            order.total().amount(),
            order.total().currency(),
            order.status()
        );
    }

    private Order toDomain(OrderJpaEntity entity) {
        return new Order(
            entity.getId(),
            entity.getCustomerId(),
            entity.getPaymentId(),
            new MoneyValue(entity.getAmount(), entity.getCurrency()),
            entity.getStatus()
        );
    }
}
