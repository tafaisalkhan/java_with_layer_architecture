package com.mycloud.productservice.adapter.out.persistence.repository;

import com.mycloud.productservice.adapter.out.persistence.ProductJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {
}
