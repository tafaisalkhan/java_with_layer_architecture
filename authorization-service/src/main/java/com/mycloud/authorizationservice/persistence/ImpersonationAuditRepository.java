package com.mycloud.authorizationservice.persistence;
import java.util.UUID; import org.springframework.data.jpa.repository.JpaRepository;
public interface ImpersonationAuditRepository extends JpaRepository<ImpersonationAuditEntity,UUID>{}
