package com.example.customerservice.adapter.out.messaging;

import com.example.common.event.CustomerCreatedEvent;
import com.example.customerservice.application.port.out.spi.CustomerEventPublisherPort;
import com.example.customerservice.domain.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCustomerEventPublisherAdapter implements CustomerEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String customersCreatedTopic;

    public KafkaCustomerEventPublisherAdapter(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${app.kafka.topics.customers-created}") String customersCreatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.customersCreatedTopic = customersCreatedTopic;
    }

    @Override
    public void publishCustomerCreated(Customer customer) {
        CustomerCreatedEvent event = new CustomerCreatedEvent(
            customer.id(),
            customer.userId(),
            customer.fullName(),
            customer.phone()
        );
        kafkaTemplate.send(customersCreatedTopic, customer.id().toString(), event);
    }
}
