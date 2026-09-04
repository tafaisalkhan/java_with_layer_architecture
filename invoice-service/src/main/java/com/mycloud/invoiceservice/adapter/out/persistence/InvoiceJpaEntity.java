package com.mycloud.invoiceservice.adapter.out.persistence;

import com.mycloud.invoiceservice.domain.InvoiceStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
public class InvoiceJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "contract_id", nullable = false)
    private UUID contractId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "issued_date", nullable = false)
    private LocalDate issuedDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private InvoiceStatus status;

    @ElementCollection
    @CollectionTable(name = "invoice_lines", joinColumns = @JoinColumn(name = "invoice_id"))
    private List<InvoiceLineJpaEmbeddable> lines = new ArrayList<>();

    protected InvoiceJpaEntity() {
    }

    public InvoiceJpaEntity(
        UUID id,
        UUID contractId,
        UUID customerId,
        LocalDate issuedDate,
        LocalDate dueDate,
        InvoiceStatus status,
        List<InvoiceLineJpaEmbeddable> lines
    ) {
        this.id = id;
        this.contractId = contractId;
        this.customerId = customerId;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.status = status;
        this.lines = new ArrayList<>(lines);
    }

    public UUID getId() {
        return id;
    }

    public UUID getContractId() {
        return contractId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public List<InvoiceLineJpaEmbeddable> getLines() {
        return lines;
    }
}
