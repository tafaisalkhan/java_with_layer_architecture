package com.mycloud.contractservice.adapter.out.persistence.repository;

import com.mycloud.contractservice.adapter.out.persistence.ContractJpaEntity;
import com.mycloud.contractservice.domain.ContractStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataContractRepository extends JpaRepository<ContractJpaEntity, UUID> {
    @Override
    @EntityGraph(attributePaths = "products")
    Optional<ContractJpaEntity> findById(UUID contractId);

    @EntityGraph(attributePaths = "products")
    Optional<ContractJpaEntity> findFirstByCustomerIdAndStatus(UUID customerId, ContractStatus status);
}
