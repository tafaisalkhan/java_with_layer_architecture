package com.mycloud.orchestratorservice.application.service;

import java.util.UUID;
import org.springframework.stereotype.Component;

/** Enforces customer-only, tenant-owned provisioning after gateway RBAC. */
@Component
public class ProvisioningTenantAccessPolicy {
    public UUID effectiveCustomerId(String effectiveAccountType, String effectiveTenantId) {
        if (!"CUSTOMER".equalsIgnoreCase(effectiveAccountType)) {
            throw new ProvisioningAccessDeniedException(
                "VM provisioning requires an effective CUSTOMER identity; administrators must impersonate a customer"
            );
        }
        try {
            return UUID.fromString(effectiveTenantId == null ? "" : effectiveTenantId);
        } catch (IllegalArgumentException exception) {
            throw new ProvisioningAccessDeniedException("a valid effective customer tenant is required");
        }
    }

    public void ensureCanProvision(UUID requestedCustomerId, String effectiveAccountType, String effectiveTenantId) {
        UUID tenantId = effectiveCustomerId(effectiveAccountType, effectiveTenantId);
        if (!requestedCustomerId.equals(tenantId)) {
            throw new ProvisioningAccessDeniedException("cannot provision resources for another customer");
        }
    }
}
