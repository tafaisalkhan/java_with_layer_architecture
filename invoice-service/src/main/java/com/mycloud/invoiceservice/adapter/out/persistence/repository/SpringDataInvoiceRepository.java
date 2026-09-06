package com.mycloud.invoiceservice.adapter.out.persistence.repository;

import com.mycloud.invoiceservice.adapter.out.persistence.InvoiceJpaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInvoiceRepository extends JpaRepository<InvoiceJpaEntity, UUID> {
    @Override
    @EntityGraph(attributePaths = "lines")
    Optional<InvoiceJpaEntity> findById(UUID invoiceId);
}
