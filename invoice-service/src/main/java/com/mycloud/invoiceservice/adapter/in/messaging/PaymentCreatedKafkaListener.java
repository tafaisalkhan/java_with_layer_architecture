package com.mycloud.invoiceservice.adapter.in.messaging;

import com.mycloud.common.event.PaymentCreatedEvent;
import com.mycloud.invoiceservice.application.port.in.usecase.RecordPaymentUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCreatedKafkaListener {
    private final RecordPaymentUseCase recordPaymentUseCase;

    public PaymentCreatedKafkaListener(RecordPaymentUseCase recordPaymentUseCase) {
        this.recordPaymentUseCase = recordPaymentUseCase;
    }

    @KafkaListener(topics = "${app.kafka.topics.payments-created}", groupId = "invoice-service")
    public void handle(PaymentCreatedEvent event) {
        recordPaymentUseCase.recordPayment(event.invoiceId());
    }
}
