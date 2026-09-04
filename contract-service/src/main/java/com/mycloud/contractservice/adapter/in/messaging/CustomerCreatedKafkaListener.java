package com.mycloud.contractservice.adapter.in.messaging;

import com.mycloud.common.event.CustomerCreatedEvent;
import com.mycloud.common.money.MoneyValue;
import com.mycloud.contractservice.adapter.out.persistence.repository.SpringDataDefaultContractConfigRepository;
import com.mycloud.contractservice.application.port.in.AddCustomerCreditCommand;
import com.mycloud.contractservice.application.port.in.usecase.AddCustomerCreditUseCase;
import com.mycloud.contractservice.application.port.out.spi.ContractEventPublisherPort;
import com.mycloud.contractservice.application.port.out.spi.ContractRepositoryPort;
import com.mycloud.contractservice.domain.Contract;
import com.mycloud.contractservice.domain.ContractProduct;
import com.mycloud.contractservice.domain.CreditType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreatedKafkaListener {
    private static final String TRIAL_CONFIG_KEY = "TRIAL";

    private final SpringDataDefaultContractConfigRepository defaultContractConfigRepository;
    private final ContractRepositoryPort contractRepositoryPort;
    private final ContractEventPublisherPort contractEventPublisherPort;
    private final AddCustomerCreditUseCase addCustomerCreditUseCase;

    public CustomerCreatedKafkaListener(
        SpringDataDefaultContractConfigRepository defaultContractConfigRepository,
        ContractRepositoryPort contractRepositoryPort,
        ContractEventPublisherPort contractEventPublisherPort,
        AddCustomerCreditUseCase addCustomerCreditUseCase
    ) {
        this.defaultContractConfigRepository = defaultContractConfigRepository;
        this.contractRepositoryPort = contractRepositoryPort;
        this.contractEventPublisherPort = contractEventPublisherPort;
        this.addCustomerCreditUseCase = addCustomerCreditUseCase;
    }

    @KafkaListener(topics = "${app.kafka.topics.customers-created}", groupId = "contract-service")
    public void handle(CustomerCreatedEvent event) {
        if (contractRepositoryPort.findActiveByCustomerId(event.customerId()).isPresent()) {
            return;
        }
        var config = defaultContractConfigRepository.findById(TRIAL_CONFIG_KEY)
            .orElseThrow(() -> new IllegalStateException("default trial contract config not found"));
        LocalDate startDate = LocalDate.now();
        Contract contract = Contract.create(
            event.customerId(),
            config.getContractType(),
            startDate,
            startDate.plusDays(config.getDurationDays()),
            List.of(ContractProduct.create(
                config.getProductId(),
                config.getProductName(),
                config.getQuantity(),
                new MoneyValue(config.getUnitAmount(), config.getCurrency())
            ))
        );
        Contract savedContract = contractRepositoryPort.save(contract);
        addCustomerCreditUseCase.addCredit(new AddCustomerCreditCommand(
            event.customerId(),
            CreditType.SIGNUP,
            config.getSignupCreditAmount(),
            config.getCurrency(),
            "Default signup credit"
        ));
        contractEventPublisherPort.publishContractActivated(savedContract);
    }
}
