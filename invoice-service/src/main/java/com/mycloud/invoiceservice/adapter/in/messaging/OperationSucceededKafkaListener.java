package com.mycloud.invoiceservice.adapter.in.messaging;

import com.mycloud.common.event.OperationSucceededEvent;
import com.mycloud.invoiceservice.application.service.BillingChargeApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OperationSucceededKafkaListener {
    private final BillingChargeApplicationService billingChargeApplicationService;

    public OperationSucceededKafkaListener(BillingChargeApplicationService billingChargeApplicationService) {
        this.billingChargeApplicationService = billingChargeApplicationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.operations-succeeded}", groupId = "invoice-service")
    public void handle(OperationSucceededEvent event) {
        billingChargeApplicationService.recordOperationSucceeded(event);
    }
}
