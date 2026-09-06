package com.mycloud.authorizationservice.persistence;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface PermissionRepository extends JpaRepository<PermissionEntity,UUID>{Optional<PermissionEntity> findByCode(String code);}
