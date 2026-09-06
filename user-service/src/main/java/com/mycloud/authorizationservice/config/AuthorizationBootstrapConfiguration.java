package com.mycloud.authorizationservice.config;

import com.mycloud.authorizationservice.application.AuthorizationService;
import com.mycloud.authorizationservice.domain.AccountType;
import com.mycloud.authorizationservice.persistence.*;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationBootstrapConfiguration {
    @Bean ApplicationRunner bootstrapAuthorization(
        @Value("${authorization.bootstrap-admin-subject:}") String subject,
        PrincipalRepository principals, RoleRepository roles, UserRoleRepository userRoles,
        AuthorizationService service
    ) {
        return ignored -> {
            if (subject == null || subject.isBlank() || principals.findByKeycloakSubject(subject).isPresent()) return;
            RoleEntity superAdmin = roles.findByNameAndTenantIdIsNull("SUPER_ADMIN")
                .orElseGet(() -> roles.save(new RoleEntity(UUID.randomUUID(), "SUPER_ADMIN", AccountType.ADMIN, null, false)));
            PrincipalEntity principal = service.createPrincipal(subject, AccountType.ADMIN, null, null);
            userRoles.save(new UserRoleEntity(UUID.randomUUID(), principal.getId(), superAdmin));
        };
    }
}
