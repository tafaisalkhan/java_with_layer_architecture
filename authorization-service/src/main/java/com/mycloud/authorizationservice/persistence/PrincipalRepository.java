package com.mycloud.authorizationservice.persistence;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface PrincipalRepository extends JpaRepository<PrincipalEntity,UUID>{Optional<PrincipalEntity> findByKeycloakSubject(String subject);}
