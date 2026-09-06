package com.mycloud.authorizationservice.persistence;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface UserPermissionRepository extends JpaRepository<UserPermissionEntity,UUID>{List<UserPermissionEntity> findByPrincipalId(UUID principalId);}
