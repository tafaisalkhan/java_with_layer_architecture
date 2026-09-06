package com.mycloud.authorizationservice.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="permissions")
public class PermissionEntity {
    @Id private UUID id;
    @Column(nullable=false,unique=true) private String code;
    @Column(nullable=false) private String httpMethod;
    @Column(nullable=false) private String pathPattern;
    protected PermissionEntity() {}
    public PermissionEntity(UUID id,String code,String method,String pattern){this.id=id;this.code=code;this.httpMethod=method;this.pathPattern=pattern;}
    public UUID getId(){return id;} public String getCode(){return code;} public String getHttpMethod(){return httpMethod;} public String getPathPattern(){return pathPattern;}
}
