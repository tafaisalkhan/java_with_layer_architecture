package com.mycloud.contractservice.application.service;

import com.mycloud.common.money.MoneyValue;
import com.mycloud.contractservice.adapter.out.persistence.CustomerCreditJpaEntity;
import com.mycloud.contractservice.adapter.out.persistence.repository.SpringDataCustomerCreditRepository;
import com.mycloud.contractservice.application.port.in.AddCustomerCreditCommand;
import com.mycloud.contractservice.application.port.in.CustomerCreditBalanceResult;
import com.mycloud.contractservice.application.port.in.CustomerCreditResult;
import com.mycloud.contractservice.application.port.in.usecase.AddCustomerCreditUseCase;
import com.mycloud.contractservice.application.port.in.usecase.GetCustomerCreditBalanceUseCase;
import com.mycloud.contractservice.domain.CustomerCredit;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CustomerCreditApplicationService implements AddCustomerCreditUseCase, GetCustomerCreditBalanceUseCase {
    private final SpringDataCustomerCreditRepository customerCreditRepository;

    public CustomerCreditApplicationService(SpringDataCustomerCreditRepository customerCreditRepository) {
        this.customerCreditRepository = customerCreditRepository;
    }

    @Override
    public CustomerCreditResult addCredit(AddCustomerCreditCommand command) {
        CustomerCredit credit = CustomerCredit.grant(
            command.customerId(),
            command.requestedType(),
            new MoneyValue(command.amount(), command.currency()),
            command.reason()
        );
        return toResult(customerCreditRepository.save(toEntity(credit)));
    }

    @Override
    public CustomerCreditBalanceResult getCreditBalance(UUID customerId) {
        List<CustomerCreditResult> credits = customerCreditRepository.findByCustomerId(customerId).stream()
            .map(this::toResult)
            .toList();
        BigDecimal remainingAmount = credits.stream()
            .map(CustomerCreditResult::remainingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        String currency = credits.stream()
            .findFirst()
            .map(CustomerCreditResult::currency)
            .orElse("USD");
        return new CustomerCreditBalanceResult(customerId, remainingAmount, currency, credits);
    }

    private CustomerCreditJpaEntity toEntity(CustomerCredit credit) {
        return new CustomerCreditJpaEntity(
            credit.id(),
            credit.customerId(),
            credit.type(),
            credit.amount().amount(),
            credit.remainingAmount().amount(),
            credit.amount().currency(),
            credit.reason(),
            credit.createdAt()
        );
    }

    private CustomerCreditResult toResult(CustomerCreditJpaEntity entity) {
        return new CustomerCreditResult(
            entity.getId(),
            entity.getCustomerId(),
            entity.getType().name(),
            entity.getAmount(),
            entity.getRemainingAmount(),
            entity.getCurrency(),
            entity.getReason(),
            entity.getCreatedAt()
        );
    }
}
