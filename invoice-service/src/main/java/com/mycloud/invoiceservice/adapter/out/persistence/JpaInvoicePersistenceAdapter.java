package com.mycloud.invoiceservice.adapter.out.persistence;

import com.mycloud.common.money.MoneyValue;
import com.mycloud.invoiceservice.adapter.out.persistence.repository.SpringDataInvoiceRepository;
import com.mycloud.invoiceservice.application.port.out.spi.InvoiceRepositoryPort;
import com.mycloud.invoiceservice.domain.Invoice;
import com.mycloud.invoiceservice.domain.InvoiceLine;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaInvoicePersistenceAdapter implements InvoiceRepositoryPort {
    private final SpringDataInvoiceRepository springDataInvoiceRepository;

    public JpaInvoicePersistenceAdapter(SpringDataInvoiceRepository springDataInvoiceRepository) {
        this.springDataInvoiceRepository = springDataInvoiceRepository;
    }

    @Override
    public Invoice save(Invoice invoice) {
        return toDomain(springDataInvoiceRepository.save(toEntity(invoice)));
    }

    @Override
    public Optional<Invoice> findById(UUID invoiceId) {
        return springDataInvoiceRepository.findById(invoiceId).map(this::toDomain);
    }

    private InvoiceJpaEntity toEntity(Invoice invoice) {
        return new InvoiceJpaEntity(
            invoice.id(),
            invoice.contractId(),
            invoice.customerId(),
            invoice.issuedDate(),
            invoice.dueDate(),
            invoice.status(),
            invoice.lines().stream().map(this::toLineEntity).toList()
        );
    }

    private Invoice toDomain(InvoiceJpaEntity entity) {
        return new Invoice(
            entity.getId(),
            entity.getContractId(),
            entity.getCustomerId(),
            entity.getIssuedDate(),
            entity.getDueDate(),
            entity.getStatus(),
            entity.getLines().stream().map(this::toLineDomain).toList()
        );
    }

    private InvoiceLineJpaEmbeddable toLineEntity(InvoiceLine line) {
        return new InvoiceLineJpaEmbeddable(
            line.productId(),
            line.productName(),
            line.quantity(),
            line.unitPrice().amount(),
            line.unitPrice().currency()
        );
    }

    private InvoiceLine toLineDomain(InvoiceLineJpaEmbeddable entity) {
        return new InvoiceLine(
            entity.getProductId(),
            entity.getProductName(),
            entity.getQuantity(),
            new MoneyValue(entity.getUnitAmount(), entity.getCurrency())
        );
    }
}
