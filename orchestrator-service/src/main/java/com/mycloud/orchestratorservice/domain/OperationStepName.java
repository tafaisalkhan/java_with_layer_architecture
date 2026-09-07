package com.mycloud.orchestratorservice.domain;

public enum OperationStepName {
    CHECK_CUSTOMER_QUOTA,
    CHECK_RESOURCE_ELIGIBILITY,
    /** Legacy persisted value; authentication now runs through the runtime session provider. */
    CREATE_UNSCOPED_TOKEN,
    LOAD_PROVIDER_CONFIGURATION,
    /** Legacy persisted value; authentication now runs through the runtime session provider. */
    CREATE_SCOPED_TOKEN,
    PROVISION_RESOURCE,
    WAIT_FOR_VM_ACTIVE,
    ASSIGN_PUBLIC_IP,
    COLLECT_RESOURCE_METADATA,
    REGISTER_MONITORING,
    COMMIT_QUOTA,
    REQUEST_BILLING,
    ROLLBACK_RESOURCE,
    RELEASE_QUOTA
}
