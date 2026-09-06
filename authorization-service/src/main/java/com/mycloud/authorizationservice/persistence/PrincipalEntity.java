package com.mycloud.authorizationservice.persistence;

import com.mycloud.authorizationservice.domain.AccountType;
import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name = "security_principals")
public class PrincipalEntity {
    @Id private UUID id;
    @Column(nullable=false, unique=true) private String keycloakSubject;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountType accountType;
    private UUID tenantId;
    private UUID parentId;
    @Column(nullable=false) private boolean active;
    protected PrincipalEntity() {}
    public PrincipalEntity(UUID id, String subject, AccountType type, UUID tenantId, UUID parentId) {
        this.id=id; this.keycloakSubject=subject; this.accountType=type; this.tenantId=tenantId; this.parentId=parentId; this.active=true;
    }
    public UUID getId(){return id;} public String getKeycloakSubject(){return keycloakSubject;}
    public AccountType getAccountType(){return accountType;} public UUID getTenantId(){return tenantId;}
    public UUID getParentId(){return parentId;} public boolean isActive(){return active;}
}
