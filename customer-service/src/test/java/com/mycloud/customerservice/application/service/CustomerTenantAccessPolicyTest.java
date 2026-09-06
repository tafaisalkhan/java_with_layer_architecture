package com.mycloud.customerservice.application.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CustomerTenantAccessPolicyTest {
    private final CustomerTenantAccessPolicy policy = new CustomerTenantAccessPolicy();

    @Test
    void allowsTheCustomerAndAnAdminImpersonatingThatCustomer() {
        UUID customerId = UUID.randomUUID();
        assertThatCode(() -> policy.ensureCanReadCustomer(customerId, "CUSTOMER", customerId.toString()))
            .doesNotThrowAnyException();
    }

    @Test
    void rejectsAnotherCustomerTenant() {
        assertThatThrownBy(() -> policy.ensureCanReadCustomer(UUID.randomUUID(), "CUSTOMER", UUID.randomUUID().toString()))
            .isInstanceOf(CustomerAccessDeniedException.class)
            .hasMessageContaining("another customer");
    }

    @Test
    void rejectsAnAdminWhoDidNotImpersonate() {
        assertThatThrownBy(() -> policy.ensureCanReadCustomer(UUID.randomUUID(), "ADMIN", null))
            .isInstanceOf(CustomerAccessDeniedException.class)
            .hasMessageContaining("CUSTOMER");
    }

    @Test
    void rejectsRequestsThatBypassTheGatewayHeaders() {
        assertThatThrownBy(() -> policy.ensureCanReadCustomer(UUID.randomUUID(), null, null))
            .isInstanceOf(CustomerAccessDeniedException.class);
    }
}
