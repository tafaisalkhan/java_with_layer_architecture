package com.mycloud.productservice.adapter.out.persistence;

import com.mycloud.productservice.domain.ProductStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductJpaEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ProductStatus status;

    @ElementCollection
    @CollectionTable(name = "product_price_history", joinColumns = @JoinColumn(name = "product_id"))
    private List<ProductPriceJpaEmbeddable> priceHistory = new ArrayList<>();

    protected ProductJpaEntity() {
    }

    public ProductJpaEntity(
        UUID id,
        String name,
        String description,
        ProductStatus status,
        List<ProductPriceJpaEmbeddable> priceHistory
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.priceHistory = new ArrayList<>(priceHistory);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public List<ProductPriceJpaEmbeddable> getPriceHistory() {
        return priceHistory;
    }
}
