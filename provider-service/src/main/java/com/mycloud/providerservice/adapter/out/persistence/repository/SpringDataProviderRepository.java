package com.mycloud.providerservice.adapter.out.persistence.repository;

import com.mycloud.providerservice.adapter.out.persistence.ProviderJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProviderRepository extends JpaRepository<ProviderJpaEntity, UUID> {
    @Override
    @EntityGraph(attributePaths = {"endpoints", "allowedCustomerIds"})
    Optional<ProviderJpaEntity> findById(UUID providerId);
}
