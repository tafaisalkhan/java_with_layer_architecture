package com.mycloud.orderservice.adapter.out.persistence;

import com.mycloud.common.money.MoneyValue;
import com.mycloud.orderservice.adapter.out.persistence.repository.SpringDataOrderRepository;
import com.mycloud.orderservice.application.port.out.spi.OrderRepositoryPort;
import com.mycloud.orderservice.domain.Order;
import com.mycloud.orderservice.domain.OrderDetail;
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
            order.status(),
            order.details().stream()
                .map(this::toDetailEntity)
                .toList()
        );
    }

    private Order toDomain(OrderJpaEntity entity) {
        return new Order(
            entity.getId(),
            entity.getCustomerId(),
            entity.getPaymentId(),
            new MoneyValue(entity.getAmount(), entity.getCurrency()),
            entity.getStatus(),
            entity.getDetails().stream()
                .map(this::toDetailDomain)
                .toList()
        );
    }

    private OrderDetailJpaEmbeddable toDetailEntity(OrderDetail detail) {
        return new OrderDetailJpaEmbeddable(
            detail.productName(),
            detail.quantity(),
            detail.unitPrice().amount(),
            detail.unitPrice().currency()
        );
    }

    private OrderDetail toDetailDomain(OrderDetailJpaEmbeddable entity) {
        return new OrderDetail(
            entity.getProductName(),
            entity.getQuantity(),
            new MoneyValue(entity.getUnitAmount(), entity.getCurrency())
        );
    }
}
