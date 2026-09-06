package com.mycloud.authorizationservice.persistence;
import com.mycloud.authorizationservice.domain.AccountType; import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface RoleRepository extends JpaRepository<RoleEntity,UUID>{List<RoleEntity> findByAccountTypeAndTenantIdIsNullAndDefaultRoleTrue(AccountType type); Optional<RoleEntity> findByNameAndTenantIdIsNull(String name);}
