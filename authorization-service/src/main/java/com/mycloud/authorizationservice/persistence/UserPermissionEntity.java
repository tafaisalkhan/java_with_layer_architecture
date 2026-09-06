package com.mycloud.authorizationservice.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="user_permissions",uniqueConstraints=@UniqueConstraint(columnNames={"principal_id","permission_id"}))
public class UserPermissionEntity {
    @Id private UUID id;
    @Column(name="principal_id",nullable=false) private UUID principalId;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="permission_id",nullable=false) private PermissionEntity permission;
    protected UserPermissionEntity() {}
    public UserPermissionEntity(UUID id,UUID principalId,PermissionEntity permission){this.id=id;this.principalId=principalId;this.permission=permission;}
    public UUID getPrincipalId(){return principalId;} public PermissionEntity getPermission(){return permission;}
}
