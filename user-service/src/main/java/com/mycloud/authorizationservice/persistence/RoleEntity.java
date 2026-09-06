package com.mycloud.authorizationservice.persistence;

import com.mycloud.authorizationservice.domain.AccountType;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity @Table(name="security_roles")
public class RoleEntity {
    @Id private UUID id;
    @Column(nullable=false) private String name;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AccountType accountType;
    private UUID tenantId;
    @Column(nullable=false) private boolean defaultRole;
    @ManyToMany(fetch=FetchType.EAGER) @JoinTable(name="role_permissions",joinColumns=@JoinColumn(name="role_id"),inverseJoinColumns=@JoinColumn(name="permission_id"))
    private Set<PermissionEntity> permissions=new HashSet<>();
    protected RoleEntity() {}
    public RoleEntity(UUID id,String name,AccountType type,UUID tenantId,boolean defaultRole){this.id=id;this.name=name;this.accountType=type;this.tenantId=tenantId;this.defaultRole=defaultRole;}
    public UUID getId(){return id;} public String getName(){return name;} public AccountType getAccountType(){return accountType;}
    public UUID getTenantId(){return tenantId;} public boolean isDefaultRole(){return defaultRole;} public Set<PermissionEntity> getPermissions(){return permissions;}
}
