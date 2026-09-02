package com.example.paymentservice.adapter.in.messaging;

import com.example.common.event.OrderCreatedEvent;
import com.example.common.money.MoneyValue;
import com.example.paymentservice.application.port.in.CreatePaymentCommand;
import com.example.paymentservice.application.port.in.usecase.CreatePaymentUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedKafkaListener {
    private final CreatePaymentUseCase createPaymentUseCase;

    public OrderCreatedKafkaListener(CreatePaymentUseCase createPaymentUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
    }

    @KafkaListener(topics = "${app.kafka.topics.orders-created}", groupId = "payment-service")
    public void handle(OrderCreatedEvent event) {
        createPaymentUseCase.createPayment(
            new CreatePaymentCommand(event.orderId(), new MoneyValue(event.amount(), event.currency()))
        );
    }
}
