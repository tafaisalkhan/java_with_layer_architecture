package com.mycloud.invoiceservice.adapter.out.persistence.repository;

import com.mycloud.invoiceservice.adapter.out.persistence.InvoiceJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInvoiceRepository extends JpaRepository<InvoiceJpaEntity, UUID> {
}
