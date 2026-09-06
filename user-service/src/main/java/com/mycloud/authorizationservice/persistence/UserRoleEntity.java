package com.mycloud.authorizationservice.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity @Table(name="user_roles",uniqueConstraints=@UniqueConstraint(columnNames={"principal_id","role_id"}))
public class UserRoleEntity {
    @Id private UUID id;
    @Column(name="principal_id",nullable=false) private UUID principalId;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="role_id",nullable=false) private RoleEntity role;
    protected UserRoleEntity() {}
    public UserRoleEntity(UUID id,UUID principalId,RoleEntity role){this.id=id;this.principalId=principalId;this.role=role;}
    public UUID getPrincipalId(){return principalId;} public RoleEntity getRole(){return role;}
}
