package com.mycloud.orchestratorservice.adapter.out.messaging;

import com.mycloud.common.event.OperationFailedEvent;
import com.mycloud.common.event.OperationSucceededEvent;
import com.mycloud.orchestratorservice.application.port.out.spi.OperationEventPublisherPort;
import com.mycloud.orchestratorservice.domain.Operation;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOperationEventPublisherAdapter implements OperationEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String operationsSucceededTopic;
    private final String operationsFailedTopic;

    public KafkaOperationEventPublisherAdapter(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${app.kafka.topics.operations-succeeded}") String operationsSucceededTopic,
        @Value("${app.kafka.topics.operations-failed}") String operationsFailedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.operationsSucceededTopic = operationsSucceededTopic;
        this.operationsFailedTopic = operationsFailedTopic;
    }

    @Override
    public void publishSucceeded(Operation operation) {
        OperationSucceededEvent event = new OperationSucceededEvent(
            operation.id(),
            operation.customerId(),
            operation.providerId(),
            operation.type().name(),
            operation.resourceType().name(),
            operation.provisionedResourceId(),
            Instant.now()
        );
        kafkaTemplate.send(operationsSucceededTopic, operation.id().toString(), event);
    }

    @Override
    public void publishFailed(Operation operation) {
        OperationFailedEvent event = new OperationFailedEvent(
            operation.id(),
            operation.customerId(),
            operation.type().name(),
            operation.resourceType().name(),
            operation.failureReason(),
            Instant.now()
        );
        kafkaTemplate.send(operationsFailedTopic, operation.id().toString(), event);
    }
}
