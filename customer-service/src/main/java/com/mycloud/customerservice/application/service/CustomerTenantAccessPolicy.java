package com.mycloud.customerservice.application.service;

import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Enforces object-level ownership after the gateway has authenticated and
 * authorized the route. The effective identity may be a real customer or an
 * administrator operating through audited customer impersonation.
 */
@Component
public class CustomerTenantAccessPolicy {
    public void ensureCanReadCustomer(UUID requestedCustomerId, String effectiveAccountType, String effectiveTenantId) {
        if (!"CUSTOMER".equalsIgnoreCase(effectiveAccountType)) {
            throw new CustomerAccessDeniedException("customer details require an effective CUSTOMER identity");
        }
        UUID tenantId;
        try {
            tenantId = UUID.fromString(effectiveTenantId == null ? "" : effectiveTenantId);
        } catch (IllegalArgumentException exception) {
            throw new CustomerAccessDeniedException("a valid effective customer tenant is required");
        }
        if (!requestedCustomerId.equals(tenantId)) {
            throw new CustomerAccessDeniedException("cannot access another customer's details");
        }
    }
}
