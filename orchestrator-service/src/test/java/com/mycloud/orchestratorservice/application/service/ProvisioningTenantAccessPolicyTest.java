package com.mycloud.orchestratorservice.application.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProvisioningTenantAccessPolicyTest {
    private final ProvisioningTenantAccessPolicy policy = new ProvisioningTenantAccessPolicy();

    @Test
    void permitsARequestForTheEffectiveCustomerTenant() {
        UUID customerId = UUID.randomUUID();
        assertThatCode(() -> policy.ensureCanProvision(customerId, "CUSTOMER", customerId.toString()))
            .doesNotThrowAnyException();
    }

    @Test
    void rejectsAnAdministratorWithoutCustomerImpersonation() {
        assertThatThrownBy(() -> policy.ensureCanProvision(UUID.randomUUID(), "ADMIN", null))
            .isInstanceOf(ProvisioningAccessDeniedException.class)
            .hasMessageContaining("impersonate");
    }

    @Test
    void rejectsProvisioningForAnotherCustomer() {
        assertThatThrownBy(() -> policy.ensureCanProvision(UUID.randomUUID(), "CUSTOMER", UUID.randomUUID().toString()))
            .isInstanceOf(ProvisioningAccessDeniedException.class)
            .hasMessageContaining("another customer");
    }

    @Test
    void rejectsRequestsThatBypassGatewayIdentityHeaders() {
        assertThatThrownBy(() -> policy.ensureCanProvision(UUID.randomUUID(), null, null))
            .isInstanceOf(ProvisioningAccessDeniedException.class);
    }
}
