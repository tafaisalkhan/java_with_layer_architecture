package com.mycloud.authorizationservice.application;

import com.mycloud.authorizationservice.domain.AccountType;
import com.mycloud.authorizationservice.persistence.*;
import java.util.*;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

@Service
public class AuthorizationService {
    private final PrincipalRepository principals; private final PermissionRepository permissions;
    private final RoleRepository roles; private final UserRoleRepository userRoles; private final UserPermissionRepository userPermissions;
    private final ImpersonationAuditRepository impersonationAudits;
    private final AntPathMatcher paths=new AntPathMatcher();
    public AuthorizationService(PrincipalRepository p,PermissionRepository x,RoleRepository r,UserRoleRepository ur,UserPermissionRepository up,ImpersonationAuditRepository audits){principals=p;permissions=x;roles=r;userRoles=ur;userPermissions=up;impersonationAudits=audits;}

    @Transactional public PrincipalEntity createPrincipal(String subject,AccountType type,UUID tenantId,UUID parentId){
        if(subject==null||subject.isBlank())throw new IllegalArgumentException("Keycloak subject is required");
        if(type==AccountType.CUSTOMER&&tenantId==null)throw new IllegalArgumentException("customer principal requires tenantId");
        if(parentId!=null){PrincipalEntity parent=principals.findById(parentId).orElseThrow(); if(parent.getAccountType()!=type)throw new IllegalArgumentException("parent and sub-admin must have the same account type"); if(type==AccountType.CUSTOMER&&!Objects.equals(parent.getTenantId(),tenantId))throw new IllegalArgumentException("customer sub-admin must belong to the parent tenant");}
        PrincipalEntity principal=principals.save(new PrincipalEntity(UUID.randomUUID(),subject,type,tenantId,parentId));
        roles.findByAccountTypeAndTenantIdIsNullAndDefaultRoleTrue(type).forEach(role->userRoles.save(new UserRoleEntity(UUID.randomUUID(),principal.getId(),role)));
        return principal;
    }
    @Transactional public PermissionEntity createPermission(String code,String method,String pattern){return permissions.save(new PermissionEntity(UUID.randomUUID(),code.toUpperCase(Locale.ROOT),method.toUpperCase(Locale.ROOT),pattern));}
    @Transactional public RoleEntity createRole(String name,AccountType type,UUID tenantId,boolean defaultRole){return roles.save(new RoleEntity(UUID.randomUUID(),name,type,tenantId,defaultRole));}
    @Transactional public void addPermissionToRole(UUID roleId,UUID permissionId){RoleEntity role=roles.findById(roleId).orElseThrow();role.getPermissions().add(permissions.findById(permissionId).orElseThrow());}
    @Transactional public void assignRole(UUID principalId,UUID roleId){PrincipalEntity principal=principals.findById(principalId).orElseThrow();RoleEntity role=roles.findById(roleId).orElseThrow();if(role.getAccountType()!=principal.getAccountType())throw new IllegalArgumentException("role account type does not match user");if(role.getTenantId()!=null&&!role.getTenantId().equals(principal.getTenantId()))throw new IllegalArgumentException("role belongs to another customer tenant");userRoles.save(new UserRoleEntity(UUID.randomUUID(),principalId,role));}
    @Transactional public void grantPermission(UUID principalId,UUID permissionId){userPermissions.save(new UserPermissionEntity(UUID.randomUUID(),principalId,permissions.findById(permissionId).orElseThrow()));}

    @Transactional public Decision check(String subject,String method,String path,UUID actAsCustomerId){
        PrincipalEntity actor=principals.findByKeycloakSubject(subject).filter(PrincipalEntity::isActive).orElse(null);
        if(actor==null)return Decision.denied("identity is not registered in authorization database");
        Set<PermissionEntity> granted=grants(actor); boolean superAdmin=userRoles.findByPrincipalId(actor.getId()).stream().anyMatch(x->x.getRole().getName().equalsIgnoreCase("SUPER_ADMIN"));
        UUID effectiveTenant=actor.getTenantId(); AccountType effectiveType=actor.getAccountType();
        if(actAsCustomerId!=null){if(actor.getAccountType()!=AccountType.ADMIN||(!superAdmin&&granted.stream().noneMatch(x->x.getCode().equals("IMPERSONATE_CUSTOMER")))){impersonationAudits.save(new ImpersonationAuditEntity(UUID.randomUUID(),actor.getId(),actAsCustomerId,method,path,false,Instant.now()));return Decision.denied("impersonation is not allowed");}effectiveTenant=actAsCustomerId;effectiveType=AccountType.CUSTOMER;}
        boolean allowed=superAdmin||granted.stream().anyMatch(permission->permission.getHttpMethod().equalsIgnoreCase(method)&&paths.match(permission.getPathPattern(),path));
        if(actAsCustomerId!=null)impersonationAudits.save(new ImpersonationAuditEntity(UUID.randomUUID(),actor.getId(),actAsCustomerId,method,path,allowed,Instant.now()));
        return new Decision(allowed,allowed?"allowed":"required permission is not granted",actor.getId(),effectiveType,effectiveTenant,actAsCustomerId!=null);
    }
    private Set<PermissionEntity> grants(PrincipalEntity principal){Set<PermissionEntity> result=new HashSet<>();userRoles.findByPrincipalId(principal.getId()).forEach(x->result.addAll(x.getRole().getPermissions()));userPermissions.findByPrincipalId(principal.getId()).forEach(x->result.add(x.getPermission()));return result;}
    public record Decision(boolean allowed,String reason,UUID actorId,AccountType effectiveAccountType,UUID effectiveTenantId,boolean impersonating){static Decision denied(String reason){return new Decision(false,reason,null,null,null,false);}}
}
