package com.mycloud.paymentservice.adapter.out.messaging;

import com.mycloud.common.event.PaymentCreatedEvent;
import com.mycloud.paymentservice.application.port.out.spi.PaymentEventPublisherPort;
import com.mycloud.paymentservice.domain.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPaymentEventPublisherAdapter implements PaymentEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String paymentsCreatedTopic;

    public KafkaPaymentEventPublisherAdapter(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${app.kafka.topics.payments-created}") String paymentsCreatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentsCreatedTopic = paymentsCreatedTopic;
    }

    @Override
    public void publishPaymentCreated(Payment payment) {
        PaymentCreatedEvent event = new PaymentCreatedEvent(
            payment.id(),
            payment.orderId(),
            payment.total().amount(),
            payment.total().currency(),
            payment.status().name()
        );
        kafkaTemplate.send(paymentsCreatedTopic, payment.orderId().toString(), event);
    }
}
