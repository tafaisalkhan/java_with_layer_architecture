package com.example.orderservice.adapter.in.messaging;

import com.example.common.event.PaymentCreatedEvent;
import com.example.orderservice.application.service.OrderApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCreatedKafkaListener {
    private final OrderApplicationService orderApplicationService;

    public PaymentCreatedKafkaListener(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.payments-created}", groupId = "order-service")
    public void handle(PaymentCreatedEvent event) {
        orderApplicationService.recordPaymentCreated(event.orderId(), event.paymentId());
    }
}
