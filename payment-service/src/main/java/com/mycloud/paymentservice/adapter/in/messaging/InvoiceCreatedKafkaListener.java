package com.mycloud.paymentservice.adapter.in.messaging;

import com.mycloud.common.event.InvoiceCreatedEvent;
import com.mycloud.common.money.MoneyValue;
import com.mycloud.paymentservice.application.port.in.CreatePaymentCommand;
import com.mycloud.paymentservice.application.port.in.usecase.CreatePaymentUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InvoiceCreatedKafkaListener {
    private final CreatePaymentUseCase createPaymentUseCase;

    public InvoiceCreatedKafkaListener(CreatePaymentUseCase createPaymentUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
    }

    @KafkaListener(topics = "${app.kafka.topics.invoices-created}", groupId = "payment-service")
    public void handle(InvoiceCreatedEvent event) {
        createPaymentUseCase.createPayment(
            new CreatePaymentCommand(event.invoiceId(), new MoneyValue(event.amount(), event.currency()))
        );
    }
}
