package com.mycloud.authorizationservice.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="impersonation_audit")
public class ImpersonationAuditEntity {
    @Id private UUID id;
    @Column(nullable=false) private UUID actorId;
    @Column(nullable=false) private UUID targetCustomerId;
    @Column(nullable=false) private String httpMethod;
    @Column(nullable=false) private String requestPath;
    @Column(nullable=false) private boolean allowed;
    @Column(nullable=false) private Instant createdAt;
    protected ImpersonationAuditEntity() {}
    public ImpersonationAuditEntity(UUID id,UUID actorId,UUID target,String method,String path,boolean allowed,Instant at){this.id=id;this.actorId=actorId;this.targetCustomerId=target;this.httpMethod=method;this.requestPath=path;this.allowed=allowed;this.createdAt=at;}
}
