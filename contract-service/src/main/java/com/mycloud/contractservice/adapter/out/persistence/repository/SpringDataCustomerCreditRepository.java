package com.mycloud.contractservice.adapter.out.persistence.repository;

import com.mycloud.contractservice.adapter.out.persistence.CustomerCreditJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCustomerCreditRepository extends JpaRepository<CustomerCreditJpaEntity, UUID> {
    List<CustomerCreditJpaEntity> findByCustomerId(UUID customerId);
}
