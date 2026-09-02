package com.mycloud.orderservice.adapter.out.messaging;

import com.mycloud.common.event.OrderCreatedEvent;
import com.mycloud.orderservice.application.port.out.spi.OrderEventPublisherPort;
import com.mycloud.orderservice.domain.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventPublisherAdapter implements OrderEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String ordersCreatedTopic;

    public KafkaOrderEventPublisherAdapter(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${app.kafka.topics.orders-created}") String ordersCreatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.ordersCreatedTopic = ordersCreatedTopic;
    }

    @Override
    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.id(),
            order.customerId(),
            order.total().amount(),
            order.total().currency()
        );
        kafkaTemplate.send(ordersCreatedTopic, order.id().toString(), event);
    }
}
