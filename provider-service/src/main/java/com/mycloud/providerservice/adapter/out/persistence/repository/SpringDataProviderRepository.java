package com.mycloud.providerservice.adapter.out.persistence.repository;

import com.mycloud.providerservice.adapter.out.persistence.ProviderJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProviderRepository extends JpaRepository<ProviderJpaEntity, UUID> {
}
