package com.mycloud.productservice.application.port.out.spi;

import com.mycloud.productservice.domain.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Product save(Product product);

    Optional<Product> findById(UUID productId);
}
