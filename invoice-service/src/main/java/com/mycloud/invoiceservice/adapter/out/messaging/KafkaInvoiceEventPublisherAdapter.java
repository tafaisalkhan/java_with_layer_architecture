package com.mycloud.invoiceservice.adapter.out.messaging;

import com.mycloud.common.event.InvoiceCreatedEvent;
import com.mycloud.invoiceservice.application.port.out.spi.InvoiceEventPublisherPort;
import com.mycloud.invoiceservice.domain.Invoice;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaInvoiceEventPublisherAdapter implements InvoiceEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String invoicesCreatedTopic;

    public KafkaInvoiceEventPublisherAdapter(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${app.kafka.topics.invoices-created}") String invoicesCreatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.invoicesCreatedTopic = invoicesCreatedTopic;
    }

    @Override
    public void publishInvoiceCreated(Invoice invoice) {
        InvoiceCreatedEvent event = new InvoiceCreatedEvent(
            invoice.id(),
            invoice.contractId(),
            invoice.customerId(),
            invoice.total().amount(),
            invoice.total().currency(),
            invoice.issuedDate(),
            invoice.dueDate()
        );
        kafkaTemplate.send(invoicesCreatedTopic, invoice.id().toString(), event);
    }
}
