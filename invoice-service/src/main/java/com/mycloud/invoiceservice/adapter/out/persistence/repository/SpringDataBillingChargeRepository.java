package com.mycloud.invoiceservice.adapter.out.persistence.repository;

import com.mycloud.invoiceservice.adapter.out.persistence.BillingChargeJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBillingChargeRepository extends JpaRepository<BillingChargeJpaEntity, UUID> {
    Optional<BillingChargeJpaEntity> findByOperationId(UUID operationId);

    List<BillingChargeJpaEntity> findByCustomerId(UUID customerId);
}
