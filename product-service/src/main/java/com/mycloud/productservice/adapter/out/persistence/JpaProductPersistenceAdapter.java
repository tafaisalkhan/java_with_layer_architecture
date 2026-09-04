package com.mycloud.productservice.adapter.out.persistence;

import com.mycloud.common.money.MoneyValue;
import com.mycloud.productservice.adapter.out.persistence.repository.SpringDataProductRepository;
import com.mycloud.productservice.application.port.out.spi.ProductRepositoryPort;
import com.mycloud.productservice.domain.Product;
import com.mycloud.productservice.domain.ProductPrice;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaProductPersistenceAdapter implements ProductRepositoryPort {
    private final SpringDataProductRepository springDataProductRepository;

    public JpaProductPersistenceAdapter(SpringDataProductRepository springDataProductRepository) {
        this.springDataProductRepository = springDataProductRepository;
    }

    @Override
    public Product save(Product product) {
        return toDomain(springDataProductRepository.save(toEntity(product)));
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return springDataProductRepository.findById(productId).map(this::toDomain);
    }

    private ProductJpaEntity toEntity(Product product) {
        return new ProductJpaEntity(
            product.id(),
            product.name(),
            product.description(),
            product.status(),
            product.priceHistory().stream().map(this::toPriceEntity).toList()
        );
    }

    private Product toDomain(ProductJpaEntity entity) {
        return new Product(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getStatus(),
            entity.getPriceHistory().stream().map(this::toPriceDomain).toList()
        );
    }

    private ProductPriceJpaEmbeddable toPriceEntity(ProductPrice price) {
        return new ProductPriceJpaEmbeddable(
            price.id(),
            price.price().amount(),
            price.price().currency(),
            price.effectiveFrom(),
            price.effectiveTo()
        );
    }

    private ProductPrice toPriceDomain(ProductPriceJpaEmbeddable entity) {
        return new ProductPrice(
            entity.getPriceId(),
            new MoneyValue(entity.getAmount(), entity.getCurrency()),
            entity.getEffectiveFrom(),
            entity.getEffectiveTo()
        );
    }
}
