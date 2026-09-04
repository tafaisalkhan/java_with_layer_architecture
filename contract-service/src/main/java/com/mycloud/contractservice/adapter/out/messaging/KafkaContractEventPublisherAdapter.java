package com.mycloud.contractservice.adapter.out.messaging;

import com.mycloud.common.event.ContractActivatedEvent;
import com.mycloud.common.event.ContractProductSnapshot;
import com.mycloud.contractservice.application.port.out.spi.ContractEventPublisherPort;
import com.mycloud.contractservice.domain.Contract;
import com.mycloud.contractservice.domain.ContractProduct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaContractEventPublisherAdapter implements ContractEventPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String contractsActivatedTopic;

    public KafkaContractEventPublisherAdapter(
        KafkaTemplate<String, Object> kafkaTemplate,
        @Value("${app.kafka.topics.contracts-activated}") String contractsActivatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.contractsActivatedTopic = contractsActivatedTopic;
    }

    @Override
    public void publishContractActivated(Contract contract) {
        ContractActivatedEvent event = new ContractActivatedEvent(
            contract.id(),
            contract.customerId(),
            contract.startDate(),
            contract.endDate(),
            contract.total().amount(),
            contract.total().currency(),
            contract.products().stream().map(this::toSnapshot).toList()
        );
        kafkaTemplate.send(contractsActivatedTopic, contract.id().toString(), event);
    }

    private ContractProductSnapshot toSnapshot(ContractProduct product) {
        return new ContractProductSnapshot(
            product.productId(),
            product.productName(),
            product.quantity(),
            product.unitPrice().amount(),
            product.unitPrice().currency(),
            product.lineAmount()
        );
    }
}
