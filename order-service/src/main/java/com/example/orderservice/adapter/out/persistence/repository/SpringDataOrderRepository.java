package com.example.orderservice.adapter.out.persistence.repository;

import com.example.orderservice.adapter.out.persistence.OrderJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {
}
