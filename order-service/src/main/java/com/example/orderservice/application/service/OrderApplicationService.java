package com.example.orderservice.application.service;

import com.example.common.query.GetByIdQuery;
import com.example.orderservice.application.port.in.CreateOrderCommand;
import com.example.orderservice.application.port.in.OrderResult;
import com.example.orderservice.application.port.in.OrderStatusView;
import com.example.orderservice.application.port.in.usecase.CreateOrderUseCase;
import com.example.orderservice.application.port.in.usecase.GetOrderUseCase;
import com.example.orderservice.application.port.out.spi.OrderEventPublisherPort;
import com.example.orderservice.application.port.out.spi.OrderRepositoryPort;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderStatus;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase {
    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderEventPublisherPort orderEventPublisherPort;

    public OrderApplicationService(
        OrderRepositoryPort orderRepositoryPort,
        OrderEventPublisherPort orderEventPublisherPort
    ) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.orderEventPublisherPort = orderEventPublisherPort;
    }

    @Override
    public OrderResult createOrder(CreateOrderCommand command) {
        Order createdOrder = Order.create(command.customerId(), command.total());
        Order savedOrder = orderRepositoryPort.save(createdOrder);
        orderEventPublisherPort.publishOrderCreated(savedOrder);
        return toResult(savedOrder);
    }

    public void recordPaymentCreated(UUID orderId, UUID paymentId) {
        Order order = orderRepositoryPort.findById(orderId)
            .orElseThrow(() -> new NoSuchElementException("order not found: " + orderId));
        orderRepositoryPort.save(order.markPaymentPending(paymentId));
    }

    @Override
    public OrderResult getOrder(GetByIdQuery query) {
        return orderRepositoryPort.findById(query.id())
            .map(this::toResult)
            .orElseThrow(() -> new NoSuchElementException("order not found: " + query.id()));
    }

    private OrderResult toResult(Order order) {
        return new OrderResult(
            order.id(),
            order.customerId(),
            order.paymentId(),
            order.total().amount(),
            order.total().currency(),
            toStatusView(order.status())
        );
    }

    private OrderStatusView toStatusView(OrderStatus status) {
        return OrderStatusView.valueOf(status.name());
    }
}
