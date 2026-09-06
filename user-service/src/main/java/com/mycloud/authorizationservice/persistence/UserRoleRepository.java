package com.mycloud.authorizationservice.persistence;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRoleRepository extends JpaRepository<UserRoleEntity,UUID>{List<UserRoleEntity> findByPrincipalId(UUID principalId);}
