package com.mycloud.orderservice.adapter.out.persistence.repository;

import com.mycloud.orderservice.adapter.out.persistence.OrderJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {
}
