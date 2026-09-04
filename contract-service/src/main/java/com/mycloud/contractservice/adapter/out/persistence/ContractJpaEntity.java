package com.mycloud.contractservice.adapter.out.persistence;

import com.mycloud.contractservice.domain.ContractStatus;
import com.mycloud.contractservice.domain.ContractType;
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
@Table(name = "contracts")
public class ContractJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 32)
    private ContractType type;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ContractStatus status;

    @ElementCollection
    @CollectionTable(name = "contract_products", joinColumns = @JoinColumn(name = "contract_id"))
    private List<ContractProductJpaEmbeddable> products = new ArrayList<>();

    protected ContractJpaEntity() {
    }

    public ContractJpaEntity(
        UUID id,
        UUID customerId,
        ContractType type,
        LocalDate startDate,
        LocalDate endDate,
        ContractStatus status,
        List<ContractProductJpaEmbeddable> products
    ) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.products = new ArrayList<>(products);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public ContractType getType() {
        return type;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public List<ContractProductJpaEmbeddable> getProducts() {
        return products;
    }
}
