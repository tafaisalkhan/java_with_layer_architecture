package com.mycloud.invoiceservice.application.service;

import com.mycloud.common.event.OperationSucceededEvent;
import com.mycloud.invoiceservice.adapter.out.persistence.BillingChargeJpaEntity;
import com.mycloud.invoiceservice.adapter.out.persistence.repository.SpringDataBillingChargeRepository;
import com.mycloud.invoiceservice.application.port.in.BillingChargeResult;
import com.mycloud.invoiceservice.application.port.in.usecase.GetCustomerBillingChargesUseCase;
import com.mycloud.invoiceservice.domain.BillingChargeStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingChargeApplicationService implements GetCustomerBillingChargesUseCase {
    private final SpringDataBillingChargeRepository billingChargeRepository;
    private final BigDecimal vmChargeAmount;
    private final String currency;

    public BillingChargeApplicationService(
        SpringDataBillingChargeRepository billingChargeRepository,
        @Value("${app.billing.vm-charge-amount:1.00}") BigDecimal vmChargeAmount,
        @Value("${app.billing.currency:USD}") String currency
    ) {
        this.billingChargeRepository = billingChargeRepository;
        this.vmChargeAmount = vmChargeAmount;
        this.currency = currency;
    }

    public void recordOperationSucceeded(OperationSucceededEvent event) {
        if (billingChargeRepository.findByOperationId(event.operationId()).isPresent()) {
            return;
        }
        BillingChargeJpaEntity charge = new BillingChargeJpaEntity(
            UUID.randomUUID(),
            event.operationId(),
            event.customerId(),
            event.resourceId(),
            event.resourceType(),
            vmChargeAmount,
            currency,
            BillingChargeStatus.PENDING_INVOICE,
            Instant.now()
        );
        billingChargeRepository.save(charge);
    }

    @Override
    public List<BillingChargeResult> getCharges(UUID customerId) {
        return billingChargeRepository.findByCustomerId(customerId).stream()
            .map(this::toResult)
            .toList();
    }

    private BillingChargeResult toResult(BillingChargeJpaEntity charge) {
        return new BillingChargeResult(
            charge.getId(),
            charge.getOperationId(),
            charge.getCustomerId(),
            charge.getResourceId(),
            charge.getResourceType(),
            charge.getAmount(),
            charge.getCurrency(),
            charge.getStatus().name(),
            charge.getCreatedAt()
        );
    }
}
