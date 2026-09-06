package com.mycloud.authorizationservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycloud.authorizationservice.application.AuthorizationService;
import com.mycloud.authorizationservice.domain.AccountType;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest @Transactional
class AuthorizationServiceIntegrationTest {
    @Autowired AuthorizationService service;

    @Test void combinesRoleAndDirectPermissionsAndIsolatesCustomerRoles(){
        UUID tenant=UUID.randomUUID();
        var user=service.createPrincipal("customer-sub",AccountType.CUSTOMER,tenant,null);
        var read=service.createPermission("CUSTOMER_READ","GET","/api/customers/**");
        var role=service.createRole("CUSTOMER_VIEWER",AccountType.CUSTOMER,tenant,false);
        service.addPermissionToRole(role.getId(),read.getId()); service.assignRole(user.getId(),role.getId());
        assertThat(service.check("customer-sub","GET","/api/customers/1",null).allowed()).isTrue();
        assertThat(service.check("customer-sub","POST","/api/providers",null).allowed()).isFalse();
        var direct=service.createPermission("OPERATION_CREATE","POST","/api/operations/**");
        service.grantPermission(user.getId(),direct.getId());
        assertThat(service.check("customer-sub","POST","/api/operations/create-vm",null).allowed()).isTrue();
    }

    @Test void adminNeedsExplicitPermissionToImpersonateCustomer(){
        var admin=service.createPrincipal("admin-sub",AccountType.ADMIN,null,null); UUID customer=UUID.randomUUID();
        assertThat(service.check("admin-sub","GET","/api/customers/1",customer).allowed()).isFalse();
        var impersonate=service.createPermission("IMPERSONATE_CUSTOMER","POST","/api/authorization/impersonation");
        var read=service.createPermission("READ_CUSTOMER","GET","/api/customers/**");
        service.grantPermission(admin.getId(),impersonate.getId()); service.grantPermission(admin.getId(),read.getId());
        var decision=service.check("admin-sub","GET","/api/customers/1",customer);
        assertThat(decision.allowed()).isTrue(); assertThat(decision.impersonating()).isTrue(); assertThat(decision.effectiveTenantId()).isEqualTo(customer);
    }
}
