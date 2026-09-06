package com.mycloud.authorizationservice.adapter;

import com.mycloud.authorizationservice.application.AuthorizationService;
import com.mycloud.authorizationservice.domain.AccountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/authorization")
public class AuthorizationController {
    private final AuthorizationService service;
    public AuthorizationController(AuthorizationService service){this.service=service;}
    @PostMapping("/check") public AuthorizationService.Decision check(@Valid @RequestBody CheckRequest request){return service.check(request.subject(),request.method(),request.path(),request.actAsCustomerId());}
    @PostMapping("/principals") public Object createPrincipal(@Valid @RequestBody PrincipalRequest r){return service.createPrincipal(r.keycloakSubject(),r.accountType(),r.tenantId(),r.parentId());}
    @PostMapping("/permissions") public Object createPermission(@Valid @RequestBody PermissionRequest r){return service.createPermission(r.code(),r.httpMethod(),r.pathPattern());}
    @PostMapping("/roles") public Object createRole(@Valid @RequestBody RoleRequest r){return service.createRole(r.name(),r.accountType(),r.tenantId(),r.defaultRole());}
    @PostMapping("/roles/{roleId}/permissions/{permissionId}") public ResponseEntity<Void> rolePermission(@PathVariable UUID roleId,@PathVariable UUID permissionId){service.addPermissionToRole(roleId,permissionId);return ResponseEntity.noContent().build();}
    @PostMapping("/principals/{principalId}/roles/{roleId}") public ResponseEntity<Void> userRole(@PathVariable UUID principalId,@PathVariable UUID roleId){service.assignRole(principalId,roleId);return ResponseEntity.noContent().build();}
    @PostMapping("/principals/{principalId}/permissions/{permissionId}") public ResponseEntity<Void> userPermission(@PathVariable UUID principalId,@PathVariable UUID permissionId){service.grantPermission(principalId,permissionId);return ResponseEntity.noContent().build();}
    public record CheckRequest(@NotBlank String subject,@NotBlank String method,@NotBlank String path,UUID actAsCustomerId){}
    public record PrincipalRequest(@NotBlank String keycloakSubject,@NotNull AccountType accountType,UUID tenantId,UUID parentId){}
    public record PermissionRequest(@NotBlank String code,@NotBlank String httpMethod,@NotBlank String pathPattern){}
    public record RoleRequest(@NotBlank String name,@NotNull AccountType accountType,UUID tenantId,boolean defaultRole){}
}
