package com.mycloud.contractservice.adapter.out.persistence.repository;

import com.mycloud.contractservice.adapter.out.persistence.DefaultContractConfigJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDefaultContractConfigRepository extends JpaRepository<DefaultContractConfigJpaEntity, String> {
}
